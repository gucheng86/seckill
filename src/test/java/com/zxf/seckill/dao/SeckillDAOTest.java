package com.zxf.seckill.dao;

import com.zxf.seckill.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

/**
 * 配置spring和junit的整合，这样junit在启动时就会加载spring容器
 */
@RunWith(SpringJUnit4ClassRunner.class)
//将class路径里的.xml文件都包括进来，那么xml文件中扫描的bean就可以获取了
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SeckillDAOTest {
    @Autowired
    private SeckillDAO seckillDAO;

    @Test
    public void reduceNumber() {
        long seckillId = 1012;
        Date date = new Date();
        int updateCount = seckillDAO.reduceNumber(seckillId, date);
        System.out.println(updateCount);
    }

    @Test
    public void queryById() {
        long seckillId = 1012;
        Seckill seckill = seckillDAO.queryById(seckillId);
        System.out.println(seckill.getName());
    }

    @Test
    public void queryAll() {
        List<Seckill> seckills = seckillDAO.queryAll(1000, 2000);
        for (Seckill seckill : seckills) {
            System.out.println(seckill.getName());
        }
    }

    @Test
    public void getConnection() throws ClassNotFoundException, IOException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/seckill?characterEncoding=utf-8&serverTimezone=Asia/Shanghai", "root", "admin");
        Statement statement = connection.createStatement();
        System.out.println(connection.isClosed());
    }
}