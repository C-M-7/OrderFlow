package com.orderflow.product;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse cancelOrder(@PathVariable Long id){
        return orderService.cancelOrder(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse showOrder(@PathVariable Long id){
        return orderService.showOrder(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<OrderResponse> listAllOrders(
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
        return orderService.listAllOrders(pageable);
    }

    @GetMapping("/customer/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public Page<OrderResponse> getOrdersByCustomer(
            @PathVariable Long customerId,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return orderService.getOrdersByCustomer(customerId, pageable);
    }

    @GetMapping("/status/{status}")
    @ResponseStatus(HttpStatus.OK)
    public Page<OrderResponse> getOrdersByStatus(
            @PathVariable OrderStatus status, 
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
        return orderService.getOrdersByStatus(status, pageable);
    }
}
