package com.zxf.seckill.exception;

public class UnderStockException extends SeckillException {

    public UnderStockException(String message) {
        super(message);
    }

    public UnderStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
