package com.yu.mall.common.model.enums;

public interface BaseEnum {
    int getValue();
    String getDesc();

    default boolean equalsValue(Integer value){
        if (value == null) {
            return false;
        }
        return getValue() == value;
    }
}
