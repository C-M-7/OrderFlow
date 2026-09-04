package com.orderflow.product.dto;

public class ProductResponse{
    private Long id;
    private String name;
    private double price;
    private Long quantity;

    public ProductResponse(Long id, String name, double price, Long quantity){
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public Long getQuantity() {
        return quantity;
    }
}