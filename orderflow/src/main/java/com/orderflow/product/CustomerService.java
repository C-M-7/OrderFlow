package com.orderflow.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderflow.product.dto.CustomerRequest;
import com.orderflow.product.dto.CustomerResponse;

@Service 
public class CustomerService {
    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }

    public CustomerResponse createCustomer(CustomerRequest customerRequest){
        log.info("Creating customer with email: {}", customerRequest.getEmail());
        Customer customer = new Customer(
            customerRequest.getName(),
            customerRequest.getPhone(),
            customerRequest.getEmail()
        );

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created successfully with id: {}", savedCustomer.getId());

        return new CustomerResponse(
            savedCustomer.getId(),
            savedCustomer.getName(),
            savedCustomer.getEmail(),
            savedCustomer.getPhone()
        );
    }

    private Customer findCustomerById(Long id){
        log.debug("Looking up customer with id: {}", id);
        return customerRepository.findById(id).orElseThrow(() -> {
            log.warn("Customer not found with id: {}", id);
            return new RuntimeException("No customer found for this id!");
        });
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

    public CustomerResponse updateCustomer(CustomerRequest newCustomerRequest, Long id){
        log.info("Updating customer with id: {}", id);
        Customer existingCustomer = findCustomerById(id);
        if(existingCustomer != null){
            existingCustomer.setName(newCustomerRequest.getName());
            existingCustomer.setPhone(newCustomerRequest.getPhone());
            existingCustomer.setEmail(newCustomerRequest.getEmail());
            
            Customer updatedCustomer = customerRepository.save(existingCustomer);
            log.info("Customer {} updated successfully", id);
            
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
        log.info("Deleting customer with id: {}", id);
        Customer existingCustomer = findCustomerById(id);

        if(existingCustomer != null){
            customerRepository.delete(existingCustomer);
            log.info("Customer {} deleted successfully", id);
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
