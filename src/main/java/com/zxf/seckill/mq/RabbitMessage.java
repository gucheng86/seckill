package com.zxf.seckill.mq;

/**
 * 发送给RabbitMQ的消息
 */
public class RabbitMessage {
    //秒杀商品的id，用户id， 秒杀商品地址
    private long seckillId;
    private long userPhone;
    private String md5;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(long userPhone) {
        this.userPhone = userPhone;
    }

    public RabbitMessage(long seckillId, long userPhone, String md5) {
        this.seckillId = seckillId;
        this.userPhone = userPhone;
        this.md5 = md5;
    }
}


