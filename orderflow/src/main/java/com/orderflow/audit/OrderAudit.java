package com.orderflow.audit;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document (collection= "order_audits")
public class OrderAudit {
    
    @Id 
    private String id;

    private Long orderId;
    private String action;
    private Instant timestamp;
    private String details;

    public OrderAudit() {}

    public OrderAudit(
            Long orderId,
            String action,
            String details) {
        this.orderId = orderId;
        this.action = action;
        this.details = details;
        this.timestamp = Instant.now();
    }

    public String getId() { return id; }
    public Long getOrderId() { return orderId; }
    public String getAction() { return action; }
    public Instant getTimestamp() { return timestamp; }
    public String getDetails() { return details; }
}
