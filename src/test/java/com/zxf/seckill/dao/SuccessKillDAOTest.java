package com.zxf.seckill.dao;

import com.zxf.seckill.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
//获取配置文件中的bean
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SuccessKillDAOTest {
    @Resource
    SuccessKilledDAO successKilledDAO;

    @Test
    public void insertSuccessKilled() {
        long seckillId = 1000L;
        long userPhone = 13476191877L;
        //插入秒杀明细记录
        int insertCount = successKilledDAO.insertSuccessKilled(seckillId, userPhone);
        System.out.println("insertCount=" + insertCount);
    }

    @Test
    public void queryByIdWithSeckill() {
        long seckillId = 1000L;
        long userPhone = 13476191877L;
        //查询秒杀明细记录以及秒杀商品
        SuccessKilled successKilled = successKilledDAO.queryByIdWithSeckill(seckillId, userPhone);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }
}