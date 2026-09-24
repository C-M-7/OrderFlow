package com.orderflow.product.dto;

import com.orderflow.product.OrderStatus;

import jakarta.validation.constraints.NotNull;

public class OrderStatusRequest {
    @NotNull(message = "Status is required")
    private OrderStatus status;
    private Long orderId;

    public OrderStatusRequest() {
    }

    public OrderStatusRequest(OrderStatus status) {
        this.status = status;
    }

    public OrderStatusRequest(Long orderId, OrderStatus status) {
        this.orderId = orderId;
        this.status = status;
    }

    public OrderStatus getStatus(){
        return status;
    }

    public void setStatus(OrderStatus status){
        this.status = status;
    }

    public Long getOrderId(){
        return this.orderId;
    }

    public void setOrderId(Long orderId){
        this.orderId = orderId;
    }
}
