package com.orderflow.product;

import org.springframework.stereotype.Service;

import com.orderflow.product.dto.CustomerRequest;
import com.orderflow.product.dto.CustomerResponse;

@Service 
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }

    public CustomerResponse createCustomer(CustomerRequest customerRequest){
        Customer customer = new Customer(
            customerRequest.getName(),
            customerRequest.getPhone(),
            customerRequest.getEmail()
        );

        Customer savedCustomer = customerRepository.save(customer);

        return new CustomerResponse(
            savedCustomer.getId(),
            savedCustomer.getName(),
            savedCustomer.getEmail(),
            savedCustomer.getPhone()
        );
    }

    private Customer findCustomerById(Long id){
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new RuntimeException("No customer found for this id!"));
        return customer;
    }

    public CustomerResponse findCustomerByIdResponse(Long id){
        Customer customer = findCustomerById(id);
        return new CustomerResponse(
            customer.getId(),
            customer.getName(),
            customer.getEmail(),
            customer.getPhone()
        );
    }

    public CustomerResponse updateCustomer( CustomerRequest newCustomerRequest, Long id){
        Customer existingCustomer = findCustomerById(id);
        if(existingCustomer != null){
            existingCustomer.setName(newCustomerRequest.getName());
            existingCustomer.setPhone(newCustomerRequest.getPhone());
            existingCustomer.setEmail(newCustomerRequest.getEmail());
            
            Customer updatedCustomer = customerRepository.save(existingCustomer);
            
            return new CustomerResponse(
                updatedCustomer.getId(),
                updatedCustomer.getName(),
                updatedCustomer.getEmail(),
                updatedCustomer.getPhone()
            );
        }
        return null;
    }

    public CustomerResponse deleteCustomer(Long id){
        Customer existingCustomer = findCustomerById(id);

        if(existingCustomer != null){
            customerRepository.delete(existingCustomer);
            return new CustomerResponse(
                existingCustomer.getId(), 
                existingCustomer.getName(), 
                existingCustomer.getEmail(), 
                existingCustomer.getPhone()
            );
        }
        return null;
    }
}
