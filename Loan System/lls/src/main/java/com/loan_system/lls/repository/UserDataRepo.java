package com.loan_system.lls.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.loan_system.lls.entity.UserData;
import java.util.List;

@Repository
public interface UserDataRepo extends JpaRepository <UserData,Integer>{
    
    Optional<UserData> findByMobile(String mobile);

    Optional<UserData> findByEmail(String email);

    // Optional<UserData>  findByUser_id(Integer user_id);


}
