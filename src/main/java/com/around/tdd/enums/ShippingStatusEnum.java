package com.around.tdd.enums;

public enum ShippingStatusEnum {
    SHIPPING_STARTED("배송시작"),
    SHIPPING_IN_PROGRESS("배송중"),
    SHIPPING_COMPLETED("배송완료");

    private final String description;

    ShippingStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}