package com.orderflow.product.dto;

import com.orderflow.product.OrderStatus;

public class OrderStatusRequest {
    private OrderStatus status;

    public OrderStatus getStatus(){
        return status;
    }

    public void setStatus(OrderStatus status){
        this.status = status;
    }
}
