package com.yu.mall.pay.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 退款订单
 * @TableName pay_refund_order
 */
@TableName(value ="pay_refund_order")
@Data
public class PayRefundOrder implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务端已支付的订单id
     */
    private Long bizOrderNo;

    /**
     * 业务端要退款的订单id，也就是子订单id
     */
    private Long bizRefundOrderNo;

    /**
     * 付款时传入的支付单号
     */
    private Long payOrderNo;

    /**
     * 退款单号，每次退款的唯一标示
     */
    private Long refundOrderNo;

    /**
     * 本次退款金额，单位分
     */
    private Integer refundAmount;

    /**
     * 总金额，单位分
     */
    private Integer totalAmount;

    /**
     * 是否是拆单退款，默认false
     */
    private Boolean isSplit;

    /**
     * 支付渠道编码
     */
    private String payChannelCode;

    /**
     * 第三方交易编码
     */
    private String resultCode;

    /**
     * 第三方交易信息
     */
    private String resultMsg;

    /**
     * 退款状态，0：未提交，1：退款中，2：退款失败，3：退款成功
     */
    private Integer status;

    /**
     * 退款渠道
     */
    private String refundChannel;

    /**
     * 业务端退款通知失败次数
     */
    private Integer notifyFailedTimes;

    /**
     * 退款接口通知状态，0：待通知，1：通知成功，2：通知中，3：通知失败
     */
    private Integer notifyStatus;

    /**
     * 退款单据创建时间
     */
    private Date createTime;

    /**
     * 退款单据修改时间
     */
    private Date updateTime;

    /**
     * 单据创建人，一般手动对账产生的单据才有值
     */
    private Long creater;

    /**
     * 单据修改人，一般手动对账产生的单据才有值
     */
    private Long updater;

    /**
     * 逻辑删除
     */
    private Boolean deleted;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}