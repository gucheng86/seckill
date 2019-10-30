package com.zxf.seckill.web;

import com.zxf.seckill.config.RabbitMQConfig;
import com.zxf.seckill.dto.Exposer;
import com.zxf.seckill.dto.SeckillExecution;
import com.zxf.seckill.dto.SeckillResult;
import com.zxf.seckill.entity.Seckill;
import com.zxf.seckill.enums.SeckillStateEnum;
import com.zxf.seckill.exception.DataRewriteException;
import com.zxf.seckill.exception.RepeatKillException;
import com.zxf.seckill.exception.SeckillCloseException;
import com.zxf.seckill.exception.UnderStockException;
import com.zxf.seckill.mq.RabbitMessage;
import com.zxf.seckill.service.SeckillService;
import com.zxf.seckill.util.RedisUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/seckill") //url: 模块/资源/{}/细分
public class SeckillController {
    @Autowired
    SeckillService seckillService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    RabbitTemplate rabbitTemplate;


    //获取商品列表
    @GetMapping("/list")
    public String list(Model model) {
        //获取列表页
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list", list);
        return "list";
    }

    //获取商品的详情
    @GetMapping("/{seckillId}/detail")
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        //商品id为空
        if (seckillId == null) {
            //redirect跳转：转发后浏览器的地址栏变为重定向后的地址，不共享之前请求的数据。
            return "redirect:/seckill/list";
        }
        //商品为空
        Seckill seckill = seckillService.getSeckillById(seckillId);
        if (seckill == null) {
            //forward跳转：转发后浏览器地址栏还是原来的地址，共享之前请求中的数据。
            return "forward:/seckill/list";
        }

        model.addAttribute("seckill", seckill);
        return "detail";
    }


    /**
     * 暴露秒杀接口的方法
     * produces属性代表返回的数据类型
     *
     * @param seckillId 秒杀商品id
     * @return SeckillResult：将返回秒杀商品地址封装为json数据的一个Vo类
     */
    @PostMapping(value = "/{seckillId}/exposer", produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> execute(@PathVariable("seckillId") Long seckillId) {
        //包含秒杀地址的json数据
        SeckillResult<Exposer> result;
        //1.成功获取秒杀地址
        try {
            //根据商品id获取秒杀地址
//            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            //使用redisConfig之后，会自动缓存商品
            Exposer exposer = seckillService.exportSeckillUrl2(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);
        }
        //2.获取秒杀地址失败，并传递异常信息
        catch (Exception e) {
            e.printStackTrace();
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }

        //返回最终的数据对象
        return result;
    }

    /**
     * 返回用户的秒杀信息，成功或失败
     *
     * @param seckillId 秒杀商品id
     * @param md5       md5加密串
     * @param phone     用户电话
     * @return 用户的秒杀信息
     * @CookieValue 将请求的Cookie数据，映射到功能处理方法的参数上。required表示是否必须包含value指定的参数。
     */
//    @PostMapping(value = "/{seckillId}/{md5}/execution", produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(
            @PathVariable("seckillId") Long seckillId,
            @PathVariable("md5") String md5,
            @CookieValue(value = "killPhone", required = false) Long phone) {

        //用户电话为空
        if (phone == null) {
            return new SeckillResult<>(false, "用户未注册");
        }

        //秒杀结果
        SeckillExecution execution;
        //根据dto直接封装出对应的数据结果
        try {
            //执行秒杀操作，使用存储过程
             execution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
            //1.秒杀执行完毕
            return new SeckillResult<>(true, execution);
        }  catch(RepeatKillException e) {   //spring事务通过异常回滚
            execution = new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<>(true, execution);
        } catch(SeckillCloseException e1) {
            execution = new SeckillExecution(seckillId, SeckillStateEnum.TIME_END);
            return new SeckillResult<>(true, execution);
        } catch (UnderStockException e2) {
            execution = new SeckillExecution(seckillId, SeckillStateEnum.UNDER_STOCK);
            return new SeckillResult<>(true, execution);
        } catch (DataRewriteException e3) {
            execution = new SeckillExecution(seckillId, SeckillStateEnum.DATA_REWRITE);
            return new SeckillResult<>(true, execution);
        }catch (Exception e4) {
            //-2.系统异常
            execution = new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<>(true, execution);
        }
    }

    @PostMapping(value = "/{seckillId}/{md5}/execution", produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> executeMQ(
            @PathVariable("seckillId") Long seckillId,
            @PathVariable("md5") String md5,
            @CookieValue(value = "killPhone", required = false) Long phone) {
        ///用户电话为空：这个部分可以用其他方法中解决，设置拦截器验证登录
        if (phone == null) {
            return new SeckillResult<>(false, "用户未注册");
        }

        //将用户的秒杀请求放入到MQ中，状态为排队中
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, new RabbitMessage(seckillId, phone, md5));

        //给前端返回排队状态
        SeckillExecution execution = SeckillExecution.unsuccess(seckillId, SeckillStateEnum.QUEUE);
        SeckillResult<SeckillExecution> result = new SeckillResult<>(true, execution);
        //记录到redis中
        redisUtil.putResult(seckillId, phone, result);

        return result;
    }

    /**
     * 从redis缓存中查询秒杀结果
     */
    @GetMapping("/{seckillId}/result")
    @ResponseBody
    public Object executeResult(@PathVariable("seckillId")long seckillId, @CookieValue(value = "killPhone", required = false) Long phone) {
        return redisUtil.getResult(seckillId, phone);
    }

        //获取系统当前时间，毫秒
    @GetMapping("/time/now")
    @ResponseBody
    public SeckillResult<Long> time(){
        Date date = new Date();
        return new SeckillResult<>(true, date.getTime());
    }
}
