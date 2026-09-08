package com.orderflow.product;

import org.springframework.web.bind.annotation.RestController;

import com.orderflow.product.dto.CustomerRequest;
import com.orderflow.product.dto.CustomerResponse;

import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@RestController 
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }
    
    @PostMapping
    public CustomerResponse createCustomer(@Valid @RequestBody CustomerRequest customerRequest){
        return customerService.createCustomer(customerRequest);
    }

    @GetMapping("/{id}")
    public CustomerResponse getCustomerById(@PathVariable Long id){
        return customerService.findCustomerByIdResponse(id);
    }

    @PutMapping("/{id}")
    public CustomerResponse updateCustomer(@Valid @RequestBody CustomerRequest customerRequest, @PathVariable Long id){
        return customerService.updateCustomer(customerRequest, id);
    }

    @DeleteMapping("/{id}")
    public CustomerResponse deleteCustomer(@PathVariable Long id){
        return customerService.deleteCustomer(id);
    }
}
