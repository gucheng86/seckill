package com.zxf.seckill.exception;

/**
 * 所有秒杀相关的业务异常
 */
public class SeckillException extends RuntimeException {
    /**用指定的详细消息构造一个新的运行时异常。原因尚未被初始化*/
    public SeckillException(String message) {
        super(message);
    }

    /**用指定的详细消息和原因构造一个新的运行时异常。*/
    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }

}
