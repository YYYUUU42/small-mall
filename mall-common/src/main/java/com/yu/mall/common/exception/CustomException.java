package com.yu.mall.common.exception;

import com.yu.mall.common.model.enums.AppHttpCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomException extends RuntimeException {

	private int code;

	private String message;

	public CustomException(AppHttpCodeEnum httpCodeEnum) {
		super(httpCodeEnum.getMessage());
		this.code = httpCodeEnum.getCode();
		this.message = httpCodeEnum.getMessage();
	}

	public CustomException(int code, String message) {
		super(message);
		this.code = code;
		this.message = message;
	}

	public CustomException(String message) {
		super(message);
		this.code = AppHttpCodeEnum.SERVER_ERROR.getCode();
		this.message = message;
	}
}
