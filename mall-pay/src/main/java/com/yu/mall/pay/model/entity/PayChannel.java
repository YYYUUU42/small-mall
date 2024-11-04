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
 * 支付渠道
 * @TableName pay_channel
 */
@TableName(value ="pay_channel")
@Data
public class PayChannel implements Serializable {
    /**
     * 支付渠道id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 支付渠道名称
     */
    private String name;

    /**
     * 支付渠道编码，用于获取支付实现
     */
    private String channelCode;

    /**
     * 渠道优先级，数字越小优先级越高
     */
    private Integer channelPriority;

    /**
     * 渠道图标
     */
    private String channelIcon;

    /**
     * 支付渠道状态，1：使用中，2：停用
     */
    private Integer status;

    /**
     * 创建人
     */
    private Long creater;

    /**
     * 更新人
     */
    private Long updater;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}