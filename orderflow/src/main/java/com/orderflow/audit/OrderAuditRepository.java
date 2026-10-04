package com.orderflow.audit;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface OrderAuditRepository extends MongoRepository<OrderAudit, String>{
    
}
