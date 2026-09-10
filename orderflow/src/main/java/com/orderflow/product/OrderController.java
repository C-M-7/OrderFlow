package com.orderflow.product;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orderflow.product.dto.OrderRequest;
import com.orderflow.product.dto.OrderResponse;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    
    @PostMapping 
    public OrderResponse createOrder(@Valid OrderRequest orderRequest){
        return orderService.createOrder(orderRequest);
    }

}
