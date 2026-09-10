package com.orderflow.product.dto;

import java.util.List;

import com.orderflow.product.Customer;

public class OrderResponse {
    private Long orderId;
    private Customer customer;
    private Double amount;
    private String status;
    private List<OrderItemResponse> items;

    public OrderResponse() {
    }

    public OrderResponse(Long orderId, Customer customer, Double amount, String status, List<OrderItemResponse> items) {
        this.orderId = orderId;
        this.customer = customer;
        this.amount = amount;
        this.status = status;
        this.items = items;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }
}

