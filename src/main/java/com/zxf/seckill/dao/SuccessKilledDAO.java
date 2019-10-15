package com.zxf.seckill.dao;

import com.zxf.seckill.entity.SuccessKilled;
import org.apache.ibatis.annotations.Param;

public interface SuccessKilledDAO {
    /**
     * 插入购买明细，可过滤重复
     * @param seckillId 秒杀商品id
     * @param userPhone 用户手机号
     * @return 插入的行数
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    /**
     * 根据秒杀商品的id查询明细SucessKilled对象（该对象包含秒杀商品对象）
     * @param seckillId
     * @param userPhone
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId, @Param("userPhone")long userPhone);

}
