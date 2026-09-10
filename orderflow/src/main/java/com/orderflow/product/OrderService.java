package com.orderflow.product;


import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.orderflow.product.dto.OrderItemRequest;
import com.orderflow.product.dto.OrderItemResponse;
import com.orderflow.product.dto.OrderRequest;
import com.orderflow.product.dto.OrderResponse;

@Service 
public class OrderService {
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderService(CustomerRepository customerRepository, ProductRepository productRepository, OrderRepository orderRepository){
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    public OrderResponse createOrder(OrderRequest orderRequest){
        Customer existingCustomer = findCustomerById(orderRequest.getCustomerId());

        if(existingCustomer == null) return null;

        Order newOrder = new Order(0.0, "Pending", existingCustomer);
        
        Double orderAmount = 0.0;
        List<OrderItemResponse> orderItemResponseList = new ArrayList<>();

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
                Double pricePerOrderItem = requestedQuant*currProdPrice;
                orderAmount += pricePerOrderItem;

                OrderItemResponse orderItemResponse = new OrderItemResponse(currProdId, currProd.getName(), requestedQuant, pricePerOrderItem);

                orderItemResponseList.add(orderItemResponse);
            }

        }
        newOrder.setAmount(orderAmount);
        newOrder.setStatus("Completed");
        orderRepository.save(newOrder);

        return new OrderResponse(
            newOrder.getId(),
            existingCustomer,
            orderAmount,
            newOrder.getStatus(),
            orderItemResponseList
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
}
