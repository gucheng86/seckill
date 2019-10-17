package com.zxf.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.zxf.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * redis的数据访问逻辑
 */
public class RedisDAO {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JedisPool jedisPool;

    public RedisDAO(String ip, int port) {
        jedisPool = new JedisPool(ip, port);
    }

    //获取class字节码，组装schema
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    /**
     * 通过redis获取Seckill对象
     * 首先创建key，根据key从redis中获取字节数组，然后反序列化到对象中
     */
    public Seckill getSeckill(long seckillId) {
        //redis操作逻辑
        try {
            //获取链接
            Jedis jedis = jedisPool.getResource();
            try{
                //实现序列化操作
                // get->byte[]->反序列化->Object[Seckill]
                // 采用第三方序列化
                String key = "seckill:" + seckillId;
                byte[] bytes = jedis.get(key.getBytes());
                //从缓存中获取
                if(bytes != null) {
                    //创建空对象
                    Seckill seckill = schema.newMessage();
                    //根据对象的schema获取对象
                    ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
                    //seckill被反序列化
                    return seckill;
                }
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    public String putSeckill(Seckill seckill) {
        // put Object[Seckill] -> 序列化 ->byte[]
        //redis操作逻辑
        try {
            //获取链接
            Jedis jedis = jedisPool.getResource();
            try{
                String key = "seckill:" + seckill.getSeckillId();
                //缓冲,获取对象的字节数组
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                //超时缓存
                int timeout = 60 * 60;  //1小时
                String result = jedis.setex(key.getBytes(), timeout, bytes);
                return result;
            } finally {
                jedis.close();
            }
            }catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
