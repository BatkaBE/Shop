package com.example.demo.config.audit;

import com.example.demo.entity.BaseEntityAudit;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AuditListener {

    @PrePersist
    public void setCreatedOn(BaseEntityAudit entity) {
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(new Date());
        }
        entity.setUpdatedAt(new Date());
    }

    @PreUpdate
    public void setUpdatedOn(BaseEntityAudit entity) {
        entity.setUpdatedAt(new Date());
    }
}