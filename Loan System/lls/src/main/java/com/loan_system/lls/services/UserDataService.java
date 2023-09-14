package com.loan_system.lls.services;

import java.sql.Timestamp;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.loan_system.lls.entity.UserData;
import com.loan_system.lls.helper.AddUserRequest;
import com.loan_system.lls.helper.ApiResponse;
import com.loan_system.lls.repository.UserDataRepo;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class UserDataService {

    private final String SUCCESS = "success";
    private static final Logger logger = LoggerFactory.getLogger(UserDataService.class);
    @Autowired
    private ApiResponse response;

    @Autowired
    private ModelMapper modelMapper;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private UserDataRepo userDataRepo;

    // ------------------ to add user only by ADMIN ----------------
    public ResponseEntity<?> addUser(AddUserRequest userRequesData, HttpServletRequest httpRequest) {
        // ------------------ setting response properties ----------------
        userRequesData.getData().setIp(httpRequest.getRemoteAddr());
        response.set_req_id(userRequesData.get_req_id());
        response.setStatus_code(HttpStatus.BAD_REQUEST.value());
        response.setStatus_msg("Email already exists in record");
        response.setData(userRequesData.getData());
        response.set_server_ts(new Timestamp(System.currentTimeMillis()));
        // ------ check if password is min 5 digit & provided email is already in DB ---
        if (userRequesData.getData().getPassword().length() < 5) {
            response.setStatus_msg("password minimum 5 character allowed.");
        } else if (!(isEmailExistInDB(userRequesData.getData().getEmail()))) {
            logger.debug("In addUser() -> UserDataService.java : start.");
            response.setStatus(SUCCESS);
            response.setStatus_code(HttpStatus.OK.value());
            response.setStatus_msg("");
            // ------------------ converting DTO to entity ----------------

            UserData addRequest = this.modelMapper.map(userRequesData.getData(), UserData.class);
            // ------------------ converting password to hash ----------------
            addRequest.setPassword(passwordEncoder.encode(userRequesData.getData().getPassword()));
            logger.debug("In addUser() -> UserDataService.java : After mapper converting: {}", addRequest.toString());
            // ------------------ save the user in db and in responce ----------------
            userDataRepo.save(addRequest);
            logger.debug("In addUser() -> UserDataService.java Request: {}, Response:{}", userRequesData, response);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    // ------------------ validate user exist in db or not ----------------
    private boolean isEmailExistInDB(String email) {
        boolean result = false;
        if (userDataRepo.findByEmail(email).isPresent()) {
            result = true;
        }
        logger.debug("In isEmailExistInDB() -> UserDataService.java : result:{}", result);
        return result;
    }
}
