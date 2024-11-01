package com.yu.mall.pay.third;


import com.yu.mall.pay.third.model.PayStatusResponse;
import com.yu.mall.pay.third.model.RefundResponse;

/**
 * 统一支付服务接口
 */
public interface IPayService {

	/**
	 * 创建预支付下单
	 *
	 * @return 对应支付宝的交易表单
	 */
	String createPrepayOrder(String title, String orderNo, Integer amount);

	/**
	 * 查看支付结果
	 */
	PayStatusResponse queryPayOrderStatus(String payOrderNo);

	/**
	 * 退款
	 */
	Boolean refundOrder(String payOrderNo, String refundOrderNo, Integer refundAmount);

	/**
	 * 查询退款状态
	 */
	RefundResponse queryRefundStatus(String orderNo, String refundOrderNo);
}
