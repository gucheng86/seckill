package com.zxf.seckill.util;

import com.zxf.seckill.exception.SeckillException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.concurrent.TimeUnit;

/**
 * 对redisTemplate的进一步封装
 */
@Component
public class RedisUtil {
    private RedisTemplate<String, Object> redisTemplate;

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }
    /*********************** 通用方法 ***********************/
    /**
     * 指定缓存的失效时间
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                return redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     */
    public void delete(String... key) {
        redisTemplate.delete(CollectionUtils.arrayToList(key));
    }

    /********************** String *************************/
    public Object get(String key) {
        if(key ==null) return null;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     */
    public boolean put(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 放入缓存并设置过期时间
     */
    public boolean put(String key,Object value,long time){
        try {
            if(time>0){
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            }else{
                put(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 给指定key的value递增值，加锁
     */
    public long decr(String key){
        long result;
        if(getLock(key)) {  //加锁成功
            result = redisTemplate.opsForValue().increment(key, -1);
            deleteLock(key);
            return result;
        } else {    //加锁失败
            int count = 5;
            while(count-- > 0) {    //间隔尝试
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //尝试获取锁
                if(getLock(key)) {
                    result = redisTemplate.opsForValue().increment(key, -1);
                    deleteLock(key);
                    return result;
                }
            }
            throw new SeckillException("现在创建的人太多了, 请稍等再试");
        }
    }

    private static final String PREFIX_LOCK = "redis_lock_";

    /**
     * 获取锁
     * @param key
     * @return
     */
     boolean getLock(String key) {
        String k = PREFIX_LOCK + key;
        boolean result = redisTemplate.opsForValue().setIfAbsent(k, "");
        if(result) expire(k, 3);
        return result;
    }

    /**
     * 删除锁
     * @param key
     */
    void deleteLock(String key) {
        redisTemplate.delete(PREFIX_LOCK + key);
    }


    /**
     * 存入秒杀商品的库存
     */
    public boolean putStock(long seckillId, Object value) {
        return put(seckillId + "_stock" , value);
    }

    public Object getStock(long seckillId) {
        return get(seckillId + "_stock");
    }


    /**
     * 存入秒杀商品的订单
     */
    public boolean putOrder(long seckillId, long userId, Object value) {
        return put(seckillId + "_" + userId + "_order", value);
    }
    public Object getOrder(long seckillId, long userId) {
        return get(seckillId + "_" + userId + "_order");
    }

    /**
     * 存入秒杀商品的执行结果
     */
    public boolean putResult(long seckillId, long userId, Object value) {
        return put(seckillId + "_" + userId + "_result", value);
    }


    public Object getResult(long seckillId, long phone) {
        return get(seckillId+"_" + phone + "_result");
    }
}
