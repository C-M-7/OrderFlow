package com.orderflow.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service 
public class OrderAuditService {
    private static final Logger log = LoggerFactory.getLogger(OrderAuditService.class);
    private final OrderAuditRepository repository;

    public OrderAuditService(OrderAuditRepository repository){
        this.repository = repository;
    }

    public void record(Long orderId, String action, String details){
        log.info("Recording audit log for orderId: {}, action: {}", orderId, action);
        OrderAudit audit = new OrderAudit(orderId, action, details);
        OrderAudit savedAudit = repository.save(audit);
        log.debug("Audit log saved with id: {}", savedAudit.getId());
    }
}
