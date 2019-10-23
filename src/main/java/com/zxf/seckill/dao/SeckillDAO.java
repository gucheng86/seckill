package com.zxf.seckill.dao;

import com.zxf.seckill.entity.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SeckillDAO {
    /**
     * 1.减少库存
     * @param seckillId 商品id
     * @param killTime 秒杀时间
     * @return 如果影响行数>1，表示更新库存的记录行数
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 根据id查询秒杀商品的信息
     * @param seckillId id
     * @return 秒杀商品对象
     */
    Seckill queryById(long seckillId);

    /**
     * 根据偏移查询秒杀商品列表
     */
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 使用存储过程执行秒杀
     * @param paramMap
     */
    void killByProcedure(Map<String, Object> paramMap);

    /**
     * 将redis缓存中的数据持久化到数据库
     * @param seckillId
     * @param Number 缓存数据
     * @return
     */
    int updateReduce(@Param("seckillId")long seckillId, @Param("number")int Number);
}
