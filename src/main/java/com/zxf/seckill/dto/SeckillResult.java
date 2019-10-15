package com.zxf.seckill.dto;

/**
 * SeckillResult表示泛型；T表示泛型的类型变量
 * @param <T> 类型变量
 */
public class SeckillResult<T> {
    //数据获取是否成功
    private boolean success;

    //传递进来的数据
    private T data;

    private String error;

    //数据获取成功
    public SeckillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    //数据获取失败
    public SeckillResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
