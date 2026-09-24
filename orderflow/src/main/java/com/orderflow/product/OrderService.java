package com.orderflow.product;


import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.orderflow.product.dto.OrderItemRequest;
import com.orderflow.product.dto.OrderItemResponse;
import com.orderflow.product.dto.OrderRequest;
import com.orderflow.product.dto.OrderResponse;
import com.orderflow.product.exception.OrderNotFoundException;

import jakarta.transaction.Transactional;

@Service 
public class OrderService {
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(CustomerRepository customerRepository, ProductRepository productRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository){
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional 
    public OrderResponse createOrder(OrderRequest orderRequest){
        Customer existingCustomer = findCustomerById(orderRequest.getCustomerId());

        if(existingCustomer == null) return null;

        Order newOrder = new Order(0.0, OrderStatus.PENDING, existingCustomer);
        
        Double orderAmount = 0.0;
        List<OrderItemResponse> orderItemResponseList = new ArrayList<>();
        List<OrderItem> orderItemsToSave = new ArrayList<>();

        for(OrderItemRequest itemRequest : orderRequest.getItems()){
            Long currProdId = itemRequest.getProductId();
            Integer requestedQuant = itemRequest.getQuantity();
            Product currProd = findProductById(currProdId);
            if(currProd == null) continue;

            Long currProdQuan = currProd.getQuantity();
            double currProdPrice = currProd.getPrice();
            if(currProdQuan >= requestedQuant){
                // checking the quantity
                Long updatedProdQuan = currProdQuan - requestedQuant;
                currProd.setQuantity(updatedProdQuan);
                productRepository.save(currProd);

                // calculating the price
                Double pricePerOrderItem = requestedQuant * currProdPrice;
                orderAmount += pricePerOrderItem;

                // prepare OrderItem entity to be saved
                OrderItem orderItem = new OrderItem(newOrder, currProd, requestedQuant, pricePerOrderItem);
                orderItemsToSave.add(orderItem);

                OrderItemResponse orderItemResponse = new OrderItemResponse(currProdId, currProd.getName(), requestedQuant, pricePerOrderItem);
                orderItemResponseList.add(orderItemResponse);
            }

        }
        newOrder.setAmount(orderAmount);
        newOrder.setStatus(OrderStatus.CONFIRMED);
        Order savedOrder = orderRepository.save(newOrder);

        // Save order items associated with savedOrder
        for(OrderItem orderItem : orderItemsToSave){
            orderItem.setOrder(savedOrder);
            orderItemRepository.save(orderItem);
        }

        return new OrderResponse(
            savedOrder.getId(),
            existingCustomer,
            orderAmount,
            savedOrder.getStatus().name(),
            orderItemResponseList
        ); 
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newOrderStatus){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        OrderStatus currentStatus = order.getStatus();

        // validate transition
        validateStatusTransition(currentStatus, newOrderStatus);

        order.setStatus(newOrderStatus);
        Order updatedOrder = orderRepository.save(order);

        return convertToResponse(updatedOrder);
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (!isValidTransition(currentStatus, newStatus)) {
            throw new IllegalStateException("Cannot change status of an order from " + currentStatus + " to " + newStatus);
        }
    }

    private OrderResponse convertToResponse(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for (OrderItem item : items) {
            itemResponses.add(new OrderItemResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPrice()
            ));
        }

        return new OrderResponse(
            order.getId(),
            order.getCustomer(),
            order.getAmount(),
            order.getStatus().name(),
            itemResponses
        );
    }

    private Customer findCustomerById(Long customerId){
        Customer existingCustomer = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("No customer found for this id"));
        return existingCustomer;
    }

    private Product findProductById(Long productId){
        Product existingProduct = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("No product found for this id"));
        return existingProduct;
    }

    private boolean isValidTransition(OrderStatus currentStatus, OrderStatus newStatus){
        if (currentStatus == null || newStatus == null) return false;
        return switch(currentStatus){
            case PENDING ->
                newStatus == OrderStatus.CONFIRMED || newStatus == OrderStatus.CANCELLED;
            case CONFIRMED ->
                newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELLED;
            case SHIPPED ->
                newStatus == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED ->
                false;
        };
    }
}
