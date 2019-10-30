package com.zxf.seckill.service;

import com.zxf.seckill.dto.Exposer;
import com.zxf.seckill.dto.SeckillExecution;
import com.zxf.seckill.entity.Seckill;
import com.zxf.seckill.exception.RepeatKillException;
import com.zxf.seckill.exception.SeckillCloseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-service.xml",
        "classpath:spring/spring-redis.xml"})
public class SeckillServiceImplTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void getSeckillList() {
        List<Seckill> seckills = seckillService.getSeckillList();
        System.out.println(seckills);
        System.out.println(redisTemplate.opsForValue().get("getSeckillList"));
    }

    @Test
    public void getSeckillById() {
        long seckillId = 1000;
        Seckill seckill = seckillService.getSeckillById(seckillId);
        System.out.println(seckill);
        System.out.println(redisTemplate.opsForValue().get("seckill_1000"));
    }

    //输出商品的秒杀地址
    @Test
    public void exportSeckillUrl() {
        long seckillId = 1000;
        //根据秒杀id使用md5输出接口
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        System.out.println(exposer);
    }

    //这个方法只有拿到了上面方法生成的秒杀地址才能进行测试，因此可以将它们合并
    @Test
    public void executeSeckill() {
        //根据秒杀商品的md5地址来进行秒杀
        long seckillId=1000;
        long userPhone=13476191876L;
        String md5="7ce8679c9ed23e3c601afe9c2d83452c";
        //重复秒杀异常
        try {
            SeckillExecution seckillExecution=seckillService.executeSeckill(seckillId,userPhone,md5);
            System.out.println(seckillExecution);
        } catch (RepeatKillException e) {
            e.printStackTrace();
        } catch (SeckillCloseException e1) {
            e1.printStackTrace();
        }
    }

    //完整的逻辑代码测试，获取秒杀地址后进行秒杀
    @Test
    public void testSeckillLogic() throws Exception {
        long seckillId = 1001;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        //如果秒杀已开启
        if(exposer.isExposed()) {
            System.out.println(exposer);

            //执行秒杀操作的参数
            long userPhone = 13476191876L;
            String md5 = exposer.getMd5();

            try{
                //执行秒杀操作
                SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, userPhone, md5);
                System.out.println(seckillExecution);
            } catch (RepeatKillException e) {
                e.printStackTrace();
            } catch (SeckillCloseException e1) {
                e1.printStackTrace();
            }
        } else {    //秒杀未开启
            System.out.println(exposer);
        }
    }

    //秒杀逻辑的事务
    @Test
    public void testSeckillProcedure() {
        long seckillId = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        //如果秒杀已开启
        if (exposer.isExposed()) {
            System.out.println(exposer);

            //执行秒杀操作的参数
            long userPhone = 13476191876L;
            String md5 = exposer.getMd5();

            try {
                SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, userPhone, md5);
                System.out.println(execution.getState() + execution.getStateInfo());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("秒杀未开始");
        }
    }

    @Test
    public void testSeckillRedis() {
        long seckillId = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        //如果秒杀已开启
        if (exposer.isExposed()) {
            System.out.println(exposer);

            //执行秒杀操作的参数
            long userPhone = 22476191844L;
            long userPhone2 = 23476191845L;
            String md5 = exposer.getMd5();
            try{
                SeckillExecution execution1 = seckillService.executeSeckillRedis(seckillId, userPhone, md5);
                System.out.println(execution1.getState() + execution1.getStateInfo());

                SeckillExecution execution2 = seckillService.executeSeckillRedis(seckillId, userPhone2, md5);
                System.out.println(execution2.getState() + execution2.getStateInfo());
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

}