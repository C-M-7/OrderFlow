package com.orderflow.audit;

import org.springframework.stereotype.Service;

@Service 
public class OrderAuditService {
    private final OrderAuditRepository repository;

    public OrderAuditService(OrderAuditRepository repository){
        this.repository = repository;
    }

    public void record(Long orderId, String action, String details){
        OrderAudit audit = new OrderAudit(orderId, action, details);
        repository.save(audit);
    }
}
