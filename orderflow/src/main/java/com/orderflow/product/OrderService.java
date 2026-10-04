package com.orderflow.product;


import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.orderflow.product.dto.OrderItemRequest;
import com.orderflow.product.dto.OrderItemResponse;
import com.orderflow.product.dto.OrderRequest;
import com.orderflow.product.dto.OrderResponse;
import com.orderflow.product.exception.InvalidOrderStatusException;
import com.orderflow.product.exception.OrderNotFoundException;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service 
public class OrderService {
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    public OrderService(CustomerRepository customerRepository, ProductRepository productRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository){
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional 
    public OrderResponse createOrder(OrderRequest orderRequest){
        log.info("Creating order for customerId: {}", orderRequest.getCustomerId());
        Customer existingCustomer = findCustomerById(orderRequest.getCustomerId());

        if(existingCustomer == null) {
            log.warn("Customer with id {} not found. Aborting order creation.", orderRequest.getCustomerId());
            return null;
        }

        Order newOrder = new Order(0.0, OrderStatus.PENDING, existingCustomer);
        
        Double orderAmount = 0.0;
        List<OrderItemResponse> orderItemResponseList = new ArrayList<>();
        List<OrderItem> orderItemsToSave = new ArrayList<>();

        for(OrderItemRequest itemRequest : orderRequest.getItems()){
            Long currProdId = itemRequest.getProductId();
            Integer requestedQuant = itemRequest.getQuantity();
            Product currProd = findProductById(currProdId);
            if(currProd == null) {
                log.warn("Product with id {} not found, skipping item", currProdId);
                continue;
            }

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
            } else {
                log.warn("Insufficient stock for productId: {}. Available: {}, Requested: {}", currProdId, currProdQuan, requestedQuant);
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

        log.info("Order created successfully with id: {} and total amount: {}", savedOrder.getId(), orderAmount);

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
        log.info("Updating order status for orderId: {} to {}", orderId, newOrderStatus);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        OrderStatus currentStatus = order.getStatus();

        // validate transition
        validateStatusTransition(currentStatus, newOrderStatus);

        order.setStatus(newOrderStatus);
        Order updatedOrder = orderRepository.save(order);
        log.info("Order {} status updated from {} to {}", orderId, currentStatus, newOrderStatus);

        return convertToResponse(updatedOrder);
    }

    @Transactional 
    public OrderResponse cancelOrder(Long orderId){
        log.info("Cancelling order with id: {}", orderId);
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        OrderStatus currentStatus = order.getStatus();

        // validate transition
        validateStatusTransition(currentStatus, OrderStatus.CANCELLED);

        // restore the product quantities
        restoreTheProductQuantities(order);

        order.setStatus(OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);
        log.info("Order {} successfully cancelled and inventory restored", orderId);

        return convertToResponse(cancelledOrder);
    }

    @Transactional
    public OrderResponse showOrder(Long orderId){
        log.debug("Fetching order details for orderId: {}", orderId);
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));

        return convertToResponse(order);    
    }

    @Transactional
    public Page<OrderResponse> listAllOrders(Pageable pageable){
        log.debug("Listing all orders with pageable: {}", pageable);
        Page<Order> allOrder = orderRepository.findAll(pageable);

        return allOrder.map(this::convertToResponse);
    }

    @Transactional
    public Page<OrderResponse> getOrdersByCustomer(Long customerId, Pageable pageable){
        log.debug("Fetching orders for customerId: {} with pageable: {}", customerId, pageable);
        findCustomerById(customerId);
        Page<Order> customerOrders = orderRepository.findByCustomerId(customerId, pageable);
        return customerOrders.map(this::convertToResponse);
    }

    @Transactional
    public Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable){
        log.debug("Fetching orders with status: {} with pageable: {}", status, pageable);
        Page<Order> statusOrders = orderRepository.findByStatus(status, pageable);
        return statusOrders.map(this::convertToResponse);
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (!isValidTransition(currentStatus, newStatus)) {
            log.error("Invalid order status transition from {} to {}", currentStatus, newStatus);
            throw new InvalidOrderStatusException(currentStatus, newStatus);
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

    private void restoreTheProductQuantities(Order order){
        Long orderId = order.getId();
        log.info("Restoring product quantities for cancelled orderId: {}", orderId);

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        for(OrderItem orderItem : items){
            Product product = orderItem.getProduct();
            long newQuantity = product.getQuantity() + orderItem.getQuantity();
            product.setQuantity(newQuantity);
            productRepository.save(product);
            log.debug("Restored {} units for productId: {}, new total: {}", orderItem.getQuantity(), product.getId(), newQuantity);
        }
    }
}
