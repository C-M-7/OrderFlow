package com.orderflow.audit;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping("/order_audit")
public class OrderAuditController {
    private final OrderAuditService auditService;

    public OrderAuditController(OrderAuditService auditService){
        this.auditService =auditService;
    }


    @PostMapping ("/test")
    public void testMongo(){
        auditService.record(101L, "ORDER_CREATED", "Testing MongoDB integration");
    }
}
