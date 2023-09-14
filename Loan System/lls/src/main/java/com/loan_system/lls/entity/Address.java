package com.loan_system.lls.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Table(
        name="address", 
        uniqueConstraints=
            @UniqueConstraint(columnNames={"user_id"})
    )
// @JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Address  {

    @Id
    @GeneratedValue
    private Integer user_id;
    @NonNull
    private String address1;
    private String address2;
    @NonNull
    private String city;
    @NonNull
    private String state;
    @NonNull
    private String country;
    @NonNull
    private String pincode;
}
