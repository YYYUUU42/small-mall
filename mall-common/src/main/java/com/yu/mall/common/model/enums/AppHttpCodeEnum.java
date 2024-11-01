package com.yu.mall.common.model.enums;

import lombok.Getter;

@Getter
public enum AppHttpCodeEnum {

	// 成功段固定为200
	SUCCESS(200, "操作成功"),
	// 登录段1~50
	NEED_LOGIN(1, "需要登录后操作"),
	LOGIN_PASSWORD_ERROR(2, "密码错误"),

	// TOKEN50~100
	TOKEN_INVALID(50, "无效的TOKEN"),
	TOKEN_EXPIRE(51, "TOKEN已过期"),
	TOKEN_REQUIRE(52, "TOKEN是必须的"),

	// 参数错误 500~1000
	PARAM_REQUIRE(500, "缺少参数"),
	PARAM_INVALID(501, "无效参数"),
	PARAM_IMAGE_FORMAT_ERROR(502, "图片格式有误"),
	SERVER_ERROR(503, "服务器内部错误"),


	// 数据错误 1000~2000
	DATA_EXIST(1000, "数据已经存在"),
	OVER_SIZE(1001, "文件大小不能超过3M"),
	UNSELECTED_FILE(1002, "未选择文件"),
	READ_EXCEL_ERROR(1003, "读取Excel文件失败"),

	DATA_NOT_EXIST(1002, "数据不存在"),
	// 数据错误 3000~3500
	NO_OPERATOR_AUTH(3000, "无权限操作"),
	NEED_ADMIN(3001, "需要管理员权限"),
	FILE_TYPE_ERROR(3002, "文件类型错误"),
	TOO_MANY_REQUEST(3003, "太多的请求次数"),


	/**
	 * 支付宝交易部分
	 */
	SIGNATURE_VERIFICATION_FAILURE(10001, "签名验证失败"),
	TRADE_FAILED(10002, "交易失败"),
	REFUND_FAILED(10003, "退款失败"),
	BUILD_PAYMENT_FORM_FAILED(10004, "构建交易表单失败"),

	;

	private int code;
	private String message;

	AppHttpCodeEnum(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
