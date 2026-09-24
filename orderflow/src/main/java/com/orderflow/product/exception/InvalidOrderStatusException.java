package com.orderflow.product.exception;

import com.orderflow.product.OrderStatus;

public class InvalidOrderStatusException extends RuntimeException {
    public InvalidOrderStatusException(OrderStatus current, OrderStatus next){
        super("Cannot change order status " + current + " to " + next);
    }
}
