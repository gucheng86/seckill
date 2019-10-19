package com.zxf.seckill.dto;

import com.zxf.seckill.entity.SuccessKilled;
import com.zxf.seckill.enums.SeckillStateEnum;

/**
 * 判断秒杀是否成功，成功就返回秒杀的信息
 */
public class SeckillExecution {
    //秒杀商品的id
    private long seckillId;

    //秒杀执行结果的状态
    private int state;

    //状态的明文标识
    private String stateInfo;

    //秒杀成功时，传递秒杀的信息
    private SuccessKilled successKilled;

    //秒杀成功，返回秒杀信息
    public SeckillExecution(long seckillId, SeckillStateEnum stateEnum, SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getInfo();
        this.successKilled = successKilled;
    }

    //秒杀失败，返回失败状态
    public SeckillExecution(long seckillId, SeckillStateEnum stateEnum) {
        this.seckillId = seckillId;
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getInfo();
    }

    @Override
    public String toString() {
        return String.format("SeckillExcution{seckillId=%s, state=%s, stateInfo=%s, succssKilled{seckillId=%s, userphone=%s, state=%s, createTime=%s}}",
                seckillId, state, stateInfo, successKilled.getSeckillId(), successKilled.getUserPhone(), successKilled.getState(), successKilled.getCreateTime());
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessKilled getSuccessKilled() {
        return successKilled;
    }

    public void setSuccessKilled(SuccessKilled successKilled) {
        this.successKilled = successKilled;
    }
}
