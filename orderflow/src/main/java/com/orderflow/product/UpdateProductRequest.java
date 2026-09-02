package com.orderflow.product;

public class UpdateProductRequest {
    private String name;
    private double price;
    private Long quantity;
    private Long id;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public double getPrice(){
        return price;
    }

    public void setName(String name){
        this.name = name;
    } 

    public void setPrice(double price){
        this.price = price;
    }

    public Long getQuantity(){
        return quantity;
    }

    public void setQuantity(Long quantity){
        this.quantity = quantity;
    }
}
