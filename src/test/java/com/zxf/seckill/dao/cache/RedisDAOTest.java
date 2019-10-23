package com.zxf.seckill.dao.cache;

import com.zxf.seckill.dao.SeckillDAO;
import com.zxf.seckill.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDAOTest {
    @Autowired
    RedisDAO redisDAO;

    @Autowired
    SeckillDAO seckillDAO;

    private long id = 1002;

    //存取秒杀商品
    @Test
    public void getandput() {
        //get and put
        Seckill seckill = redisDAO.getSeckill(id);
        if(seckill == null) {
            seckill = seckillDAO.queryById(id);
            if(seckill != null) {
                String result = redisDAO.putSeckill(seckill);
                System.out.println(result);
                seckill = redisDAO.getSeckill(id);
                System.out.println(seckill);
            }
        }
    }

    //通过缓存的商品对象减少商品库存
    @Test
    public void reduceBySeckill() {
        //1.查询seckill
        final long seckillId = 1001;
        Seckill seckill = seckillDAO.queryById(seckillId);
        System.out.println("商品库存：" + seckill.getNumber());

        //2.将商品放入redis中共
        redisDAO.putSeckill(seckill);

        //3.让多个线程执行减库存操作
        redisDAO.isReduceSeckill(seckillId);

        seckill = redisDAO.getSeckill(seckillId);
        System.out.println("商品库存：" + seckill.getNumber());

//        System.out.println("商品库存：" + seckillDAO.queryById(seckillId).getNumber());
    }

    //通过缓存的商品库存减少库存
    @Test
    public void reduceStock() throws InterruptedException {
        final long seckillId = 1000;
        Seckill seckill = seckillDAO.queryById(seckillId);
        System.out.println(seckill.getNumber());
        //存入缓存
        redisDAO.putStock(seckillId, "" + seckill.getNumber());

        for(int i = 0; i < 10; i++) {
            Thread thread = new Thread() {
                public void run() {
                    System.out.println("stock:" + redisDAO.reduceStock(seckillId));
                }
            };
            thread.start();
            thread.join();
        }

        //减少缓存
//        redisDAO.reduceStock(seckillId);

        //获取缓存
        System.out.println(redisDAO.getStock(seckillId));



    }



}