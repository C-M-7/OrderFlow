package com.orderflow.product.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class OrderRequest {
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotEmpty(message = "Items list cannot be empty")
    @Valid
    private List<OrderItemRequest> items;

    public OrderRequest() {
    }

    public OrderRequest(Long customerId, List<OrderItemRequest> items) {
        this.customerId = customerId;
        this.items = items;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}
