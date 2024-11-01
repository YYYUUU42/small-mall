package com.yu.mall.common.result;

import com.yu.mall.common.model.enums.AppHttpCodeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回类
 */
@Data
public class ResponseResult<T> implements Serializable {
    private Integer code;
    private String message;
    private T data;

    // 默认构造函数，直接返回成功状态
    public ResponseResult() {
        this(AppHttpCodeEnum.SUCCESS);
    }

    // 根据枚举类型构造
    public ResponseResult(AppHttpCodeEnum httpCodeEnum) {
        this(httpCodeEnum.getCode(), httpCodeEnum.getMessage());
    }

    // 根据 code 和 message 构造
    public ResponseResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    // 根据 code 和 data 构造
    public ResponseResult(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    // 根据 code, message, 和 data 构造
    public ResponseResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 静态方法 - 返回错误结果
    public static <T> ResponseResult<T> errorResult(int code, String message) {
        return new ResponseResult<>(code, message);
    }

    // 静态方法 - 返回成功结果
    public static <T> ResponseResult<T> okResult() {
        return new ResponseResult<>(AppHttpCodeEnum.SUCCESS);
    }

    // 静态方法 - 返回指定 code 和 message 的成功结果
    public static <T> ResponseResult<T> okResult(int code, String message) {
        return new ResponseResult<>(code, message);
    }

    // 静态方法 - 返回成功结果并附带数据
    public static <T> ResponseResult<T> okResult(T data) {
        ResponseResult<T> result = new ResponseResult<>(AppHttpCodeEnum.SUCCESS);
        result.setData(data);
        return result;
    }

    // 静态方法 - 根据枚举类型返回错误结果
    public static <T> ResponseResult<T> errorResult(AppHttpCodeEnum httpCodeEnum) {
        return new ResponseResult<>(httpCodeEnum);
    }

    // 静态方法 - 根据枚举类型和自定义消息返回错误结果
    public static <T> ResponseResult<T> errorResult(AppHttpCodeEnum httpCodeEnum, String message) {
        return new ResponseResult<>(httpCodeEnum.getCode(), message);
    }

    // 静态方法 - 设置枚举类型并返回结果
    public static <T> ResponseResult<T> setAppHttpCodeEnum(AppHttpCodeEnum httpCodeEnum) {
        return new ResponseResult<>(httpCodeEnum);
    }

    // 静态方法 - 设置枚举类型和自定义消息并返回结果
    public static <T> ResponseResult<T> setAppHttpCodeEnum(AppHttpCodeEnum httpCodeEnum, String message) {
        return new ResponseResult<>(httpCodeEnum.getCode(), message);
    }

    // 错误方法，返回链式调用
    public ResponseResult<T> error(Integer code, String message) {
        this.code = code;
        this.message = message;
        return this;
    }

    public ResponseResult<T> error(String message) {
        this.code = AppHttpCodeEnum.SERVER_ERROR.getCode();
        this.message = message;
        return this;
    }

    public ResponseResult<T> ok(T data) {
        this.code = AppHttpCodeEnum.SUCCESS.getCode();
        this.data = data;
        return this;
    }

    // 成功方法，返回链式调用
    public ResponseResult<T> ok(Integer code, T data) {
        this.code = code;
        this.data = data;
        return this;
    }

    // 成功方法，返回链式调用
    public ResponseResult<T> ok(Integer code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
        return this;
    }
}
