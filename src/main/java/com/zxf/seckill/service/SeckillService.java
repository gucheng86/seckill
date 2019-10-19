package com.zxf.seckill.service;

import com.zxf.seckill.dto.Exposer;
import com.zxf.seckill.dto.SeckillExecution;
import com.zxf.seckill.entity.Seckill;
import com.zxf.seckill.exception.RepeatKillException;
import com.zxf.seckill.exception.SeckillCloseException;
import com.zxf.seckill.exception.SeckillException;

import java.util.List;

public interface SeckillService {
    /**
     * 查询全部的秒杀商品
     * @return
     */
    List<Seckill>  getSeckillList();

    /**
     * 查询单个秒杀商品
     * @param seckillId 秒杀商品id
     * @return
     */
    Seckill getSeckillById(long seckillId);

    /**
     * 在秒杀开启时输出秒杀接口的地址，否则输出系统时间和秒杀时间
     * @param seckillId 秒杀商品id
     * @return 秒杀地址
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作的结果，
     * @param seckillId 秒杀商品id
     * @param userPhone 用户
     * @param md5
     * @return 成功则返回成功信息，失败返回异常
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException;


    /**
     * 按照存储过程执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5);


}
