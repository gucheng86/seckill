package com.zxf.seckill.enums;

public enum SeckillStateEnum {
    SUCCESS(1, "秒杀成功"),
    END(0, "秒杀结束"),
    REPEAT_KILL(-1, "秒杀重复"),
    INNER_ERROR(-2, "系统异常"),
    DATA_REWRITE(-3, "数据篡改");

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
    public static SeckillStateEnum stateOf(int state) {
        for(SeckillStateEnum seckillStateEnum : values()) {
            if(seckillStateEnum.getState() == state) {
                return seckillStateEnum;
            }
        }

        return null;
    }
}
