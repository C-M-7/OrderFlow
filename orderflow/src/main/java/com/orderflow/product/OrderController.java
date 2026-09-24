package com.orderflow.product;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.orderflow.product.dto.OrderRequest;
import com.orderflow.product.dto.OrderResponse;
import com.orderflow.product.dto.OrderStatusRequest;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    
    @PostMapping 
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody OrderRequest orderRequest){
        return orderService.createOrder(orderRequest);
    }

    @PostMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse updateOrderStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusRequest orderStatusRequest){
        return orderService.updateOrderStatus(id, orderStatusRequest.getStatus());
    } 
}
