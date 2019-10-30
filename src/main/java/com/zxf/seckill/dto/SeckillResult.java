package com.zxf.seckill.dto;

import com.zxf.seckill.enums.SeckillStateEnum;

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

    private SeckillResult() {}

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

    /*** 使用静态方法给前端返回结果 **/
    public static <T> SeckillResult success(T data) {
        SeckillResult<T> result = new SeckillResult<>();
        result.setSuccess(true);
        result.setData(data);
        return result;
    }
    public static SeckillResult error(String error) {
        SeckillResult result = new SeckillResult();
        result.setSuccess(false);
        result.setError(error);
        return result;
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

    public static void main(String[] args) {
        SeckillExecution execution = SeckillExecution.unsuccess(1000, SeckillStateEnum.QUEUE);
        SeckillResult<SeckillExecution> success = SeckillResult.success(execution);
        SeckillExecution data = success.getData();
        System.out.println(data.getState());
    }
}
