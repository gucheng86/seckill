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

}