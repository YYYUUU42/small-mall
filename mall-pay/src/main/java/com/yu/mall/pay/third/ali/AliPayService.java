package com.yu.mall.pay.third.ali;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.yu.mall.common.exception.CustomException;
import com.yu.mall.common.model.enums.AppHttpCodeEnum;
import com.yu.mall.pay.config.AliPayConfigProperties;
import com.yu.mall.pay.constants.PayConstants;
import com.yu.mall.pay.third.IPayService;
import com.yu.mall.pay.third.model.PayStatusResponse;
import com.yu.mall.pay.third.model.RefundResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;


@Slf4j
@Service
@RequiredArgsConstructor
public class AliPayService implements IPayService {

	private final AlipayClient alipayClient;
	private final AliPayConfigProperties aliPayConfigProperties;

	private static final String PRODUCT_CODE = "FAST_INSTANT_TRADE_PAY";


	/**
	 * 创建预支付下单
	 *
	 * @return 对应支付宝的交易表单
	 */
	@Override
	public String createPrepayOrder(String subject, String outTradeNo, Integer totalAmount) {
		String form = "";

		try {
			// 构建支付请求
			AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
			request.setNotifyUrl(aliPayConfigProperties.getNotifyUrl() + PayConstants.ALI_CHANNEL_CODE);
			request.setReturnUrl(aliPayConfigProperties.getReturnUrl());

			// 构建业务参数
			JSONObject bizContent = new JSONObject();
			bizContent.put("subject", subject);
			bizContent.put("out_trade_no", outTradeNo);
			bizContent.put("total_amount", transferAmount2String(totalAmount));
			bizContent.put("product_code", PRODUCT_CODE);
			request.setBizContent(bizContent.toString());

			// 执行请求
			form = alipayClient.pageExecute(request).getBody();

		} catch (Exception e) {
			throw new CustomException(AppHttpCodeEnum.BUILD_PAYMENT_FORM_FAILED);
		}

		return form;
	}


	/**
	 * 查看支付结果
	 */
	@Override
	public PayStatusResponse queryPayOrderStatus(String payOrderNo) {
		return null;
	}

	/**
	 * 退款操作
	 *
	 * @param payOrderNo    支付订单号
	 * @param refundOrderNo 退款订单号
	 * @param refundAmount  退款金额
	 * @return 退款是否成功
	 */
	@Override
	public Boolean refundOrder(String payOrderNo, String refundOrderNo, Integer refundAmount) {
		try {
			// 创建 Refund Request，设置参数
			AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
			JSONObject bizContent = new JSONObject();
			bizContent.put("trade_no", payOrderNo);
			bizContent.put("out_request_no", refundOrderNo);
			bizContent.put("refund_amount", refundAmount);

			// 可选：返回参数选项，按需传入
			// JSONArray queryOptions = new JSONArray();
			// queryOptions.add("refund_detail_item_list");
			// bizContent.put("query_options", queryOptions);

			request.setBizContent(bizContent.toString());

			// 执行退款请求
			AlipayTradeRefundResponse response = alipayClient.execute(request);

			if (response.isSuccess()) {
				log.info("支付宝退款请求成功，支付订单号: {}, 退款订单号: {}", payOrderNo, refundOrderNo);
				return true;
			} else {
				log.error("支付宝退款请求失败，支付订单号: {}, 退款订单号: {}, 响应: {}", payOrderNo, refundOrderNo, response.getBody());
				return false;
			}

		} catch (AlipayApiException e) {
			log.error("支付宝退款请求异常，支付订单号: {}, 退款订单号: {}, 原因: {}", payOrderNo, refundOrderNo, e.getMessage(), e);
			return false;
		}
	}

	/**
	 * 查询退款状态
	 */
	@Override
	public RefundResponse queryRefundStatus(String orderNo, String refundOrderNo) {
		return null;
	}


	/**
	 * 将两位小数的转为 long类型，单位为分
	 */
	public static long transferStringAmount2Int(String totalAmount) {
		return new BigDecimal(totalAmount).multiply(BigDecimal.valueOf(100)).intValue();
	}

	/**
	 * 将单位为long的整数转换为两位小数的交易数额
	 */
	public static String transferAmount2String(Integer amount) {
		BigDecimal b = new BigDecimal(amount);
		BigDecimal result = b.divide(new BigDecimal(100), new MathContext(2, RoundingMode.HALF_UP));
		return result.toString();
	}
}
