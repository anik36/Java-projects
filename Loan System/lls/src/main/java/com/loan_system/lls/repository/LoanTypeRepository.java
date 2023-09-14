package com.loan_system.lls.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.loan_system.lls.entity.LoanType;

@Repository
public interface LoanTypeRepository extends JpaRepository <LoanType,Integer>{
    
    @Query(value = "select loan_type from loan_type",nativeQuery = true)
    public List<String> findAllLoanType();

}
