package com.zxf.seckill.web;

import com.zxf.seckill.dto.Exposer;
import com.zxf.seckill.dto.SeckillExecution;
import com.zxf.seckill.dto.SeckillResult;
import com.zxf.seckill.entity.Seckill;
import com.zxf.seckill.enums.SeckillStateEnum;
import com.zxf.seckill.exception.RepeatKillException;
import com.zxf.seckill.exception.SeckillCloseException;
import com.zxf.seckill.service.SeckillService;
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
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
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
    @PostMapping(value = "/{seckillId}/{md5}/execution", produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(
            @PathVariable("seckillId") Long seckillId,
            @PathVariable("md5") String md5,
            @CookieValue(value = "killPhone", required = false) Long phone) {

        //用户电话为空
        if (phone == null) {
            return new SeckillResult<SeckillExecution>(false, "用户未注册");
        }

        try {
            //执行秒杀操作
            SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, phone, md5);
            //秒杀成功
            return new SeckillResult<SeckillExecution>(true, seckillExecution);
        } catch (RepeatKillException e1) {
            //重复秒杀
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(false, execution);
        } catch (SeckillCloseException e2) {
            //秒杀关闭
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.END);
            return new SeckillResult<SeckillExecution>(false, execution);
        } catch (Exception e3) {
            //系统异常
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(false, execution);
        }
    }

    //获取系统当前时间，毫秒
    @GetMapping("/time/now")
    public SeckillResult<Long> time(){
        Date date = new Date();
        return new SeckillResult<Long>(true, date.getTime());
    }
}
