package com.loan_system.lls.audit;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Column(name = "created_by",updatable = false, nullable = false)
    @CreatedBy
    // @Value("${some.key:007}")
    private String created_by;

    @CreationTimestamp
    @Column(name = "created_at",updatable = false)
    private Timestamp created_at;

    @Column(name = "updated_by", nullable = false)
    @LastModifiedBy
    // @Value("${some.key:007}")
    private String updated_by;

    @UpdateTimestamp
    @Column(name = "updated_at",updatable = true)
    private Timestamp updated_at;

    public String getCreated_by() {
        return created_by;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }


    
}
