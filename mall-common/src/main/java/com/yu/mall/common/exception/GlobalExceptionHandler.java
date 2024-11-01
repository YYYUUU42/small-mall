package com.yu.mall.common.exception;


import com.yu.mall.common.model.enums.AppHttpCodeEnum;
import com.yu.mall.common.result.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	/**
	 * 处理自定义业务异常
	 *
	 * @param e 自定义异常对象
	 * @return 返回封装的错误信息
	 */
	@ExceptionHandler(CustomException.class)
	@ResponseBody
	public ResponseResult<?> handleCustomException(CustomException e) {
		// 打印异常日志，使用 warn 级别记录业务异常
		log.warn("业务异常！异常信息：{}，异常代码：{}", e.getMessage(), e.getCode(), e);
		// 返回自定义异常信息
		return ResponseResult.errorResult(e.getCode(), e.getMessage());
	}

	/**
	 * 处理系统级别的异常
	 *
	 * @param e 系统异常对象
	 * @return 返回封装的错误信息
	 */
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ResponseResult<?> handleSystemException(Exception e) {
		// 打印异常日志，使用 error 级别记录系统异常
		log.error("系统异常！异常信息：{}", e.getMessage(), e);
		// 返回系统异常的通用错误信息
		return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
	}

	/**
	 * 处理空指针异常
	 *
	 * @param e 空指针异常对象
	 * @return 返回封装的错误信息
	 */
	@ExceptionHandler(NullPointerException.class)
	@ResponseBody
	public ResponseResult<?> handleNullPointerException(NullPointerException e) {
		// 打印异常日志
		log.error("空指针异常！", e);
		// 返回空指针异常的通用错误信息
		return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "空指针异常，请检查代码逻辑！");
	}

	/**
	 * 处理非法参数异常
	 *
	 * @param e 非法参数异常对象
	 * @return 返回封装的错误信息
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseBody
	public ResponseResult<?> handleIllegalArgumentException(IllegalArgumentException e) {
		// 打印异常日志
		log.warn("非法参数异常！异常信息：{}", e.getMessage(), e);
		// 返回非法参数异常的通用错误信息
		return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "请求参数不合法！");
	}
}
