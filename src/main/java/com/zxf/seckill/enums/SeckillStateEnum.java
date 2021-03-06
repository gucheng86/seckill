package com.zxf.seckill.enums;

public enum SeckillStateEnum {
    SUCCESS(1, "秒杀成功"),
    QUEUE(0, "排队中"),
    TIME_END(-1, "秒杀结束"),
    REPEAT_KILL(-2, "秒杀重复"),
    UNDER_STOCK(-3, "库存不足"),
    INNER_ERROR(-4, "系统异常"),
    DATA_REWRITE(-5, "数据篡改");

    private int state;
    private  String info;

    SeckillStateEnum(int state, String info) {
        this.state = state;
        this.info = info;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * 定义一个静态方法，通过state返回枚举常量对象
     */
    public static SeckillStateEnum valueOf(int state) {
        for(SeckillStateEnum seckillStateEnum : values()) {
            if(seckillStateEnum.getState() == state) {
                return seckillStateEnum;
            }
        }

        return null;
    }
}
