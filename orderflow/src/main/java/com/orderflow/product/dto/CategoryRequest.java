package com.orderflow.product.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoryRequest {
    
    @NotBlank(message = "Category name cant be blank")
    private String name;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
}
