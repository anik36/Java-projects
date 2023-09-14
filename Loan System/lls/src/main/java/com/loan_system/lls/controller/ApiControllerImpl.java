package com.loan_system.lls.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loan_system.lls.helper.AddUserRequest;
import com.loan_system.lls.helper.ApiRequest;
import com.loan_system.lls.helper.ApiRequestForList;
import com.loan_system.lls.services.EmailService;
import com.loan_system.lls.services.LoanService;
import com.loan_system.lls.services.UserDataService;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class ApiControllerImpl implements ApiController {

    private static final Logger logger = LoggerFactory.getLogger(ApiControllerImpl.class);

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private EmailService emailService;

    // 1. API endpoint: api/login
    // localhost:8080/api/login
    @PostMapping("/login")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    // @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> login(@RequestBody ApiRequest apiRequest, HttpServletRequest httpRequest) {
        System.out.println(apiRequest.toString());
        apiRequest.set_client_ts(new Timestamp(System.currentTimeMillis()));
        apiRequest.set_req_id(String.valueOf(UUID.randomUUID()));
        apiRequest.set_client_type(getClientType(httpRequest));
        logger.debug("ApiControllerImpl.java inside login() Request : {}", apiRequest);
        return emailService.loginAuth(apiRequest);
    }

    // 2. API endpoint: api/add_customer
    // localhost:8080/api/add_customer
    @PostMapping("/add_customer")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> addUser(@RequestBody AddUserRequest requestUserData, HttpServletRequest httpRequest) {
        requestUserData.set_client_ts(new Timestamp(System.currentTimeMillis()));
        requestUserData.set_req_id(String.valueOf(UUID.randomUUID()));
        requestUserData.set_client_type(getClientType(httpRequest));
        logger.debug("ApiControllerImpl.java inside addUser() Request : {}", requestUserData);
        return userDataService.addUser(requestUserData, httpRequest);
    }

    // 3. API endpoint: api/sent_otp
    // localhost:8080/api/sent_otp
    @PostMapping("/sent_otp")
    // @PreAuthorize("hasAuthority('ADMIN')")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<?> sendOtp(@RequestBody ApiRequest apiRequest, HttpServletRequest httpRequest) {
        apiRequest.set_client_ts(new Timestamp(System.currentTimeMillis()));
        apiRequest.set_req_id(String.valueOf(UUID.randomUUID()));
        apiRequest.set_client_type(getClientType(httpRequest));
        logger.debug("ApiControllerImpl.java inside sendOtp() Request : {}", apiRequest);
        return emailService.sendOtpOnEmail(apiRequest);
    }

    // 4. API endpoint: api/verify_otp
    // localhost:8080/api/verify_otp
    @PostMapping("/verify_otp")
    // @PreAuthorize("hasAuthority('ADMIN')")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<?> verifyOtp(@RequestBody ApiRequest apiRequest, HttpServletRequest httpRequest) {
        apiRequest.set_client_ts(new Timestamp(System.currentTimeMillis()));
        apiRequest.set_req_id(String.valueOf(UUID.randomUUID()));
        apiRequest.set_client_type(getClientType(httpRequest));
        logger.debug("ApiControllerImpl.java inside verifyOtp() Request : {}", apiRequest);
        return emailService.verifyOtp(apiRequest, httpRequest);
    }

    // 5. api/import_loan_types
    // localhost:8080/api/import_loan_types
    @PostMapping("/import_loan_types")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> importLoanCsv(@RequestBody ApiRequest apiRequest, HttpServletRequest httpRequest) {
        apiRequest.set_client_ts(new Timestamp(System.currentTimeMillis()));
        apiRequest.set_req_id(String.valueOf(UUID.randomUUID()));
        apiRequest.set_client_type(getClientType(httpRequest));
        logger.debug("ApiControllerImpl.java inside importLoanCsv() Request : {}", apiRequest);
        return loanService.importLoanCsv(apiRequest);
    }

    // 6. API endpoint: api/request_loan
    // localhost:8080/api/request_loan
    @PostMapping("/request_loan")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> requestLoan(@RequestBody ApiRequest apiRequest, HttpServletRequest httpRequest) {
        apiRequest.set_client_ts(new Timestamp(System.currentTimeMillis()));
        apiRequest.set_req_id(String.valueOf(UUID.randomUUID()));
        apiRequest.set_client_type(getClientType(httpRequest));
        logger.debug("ApiControllerImpl.java inside requestLoan() Request : {}", apiRequest);
        return loanService.requestLoan(apiRequest, httpRequest);
    }

    // 7. API endpoint: api/list_loans 
    // localhost:8080/api/list_loans
    @PostMapping("/list_loans")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<?> listLoans(@RequestBody ApiRequestForList loanRequest,
            @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest httpRequest,
            HttpServletResponse httpServletResponse)
            throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
        ResponseEntity<?> tempResponse;
        loanRequest.set_client_ts(new Timestamp(System.currentTimeMillis()));
        loanRequest.set_req_id(String.valueOf(UUID.randomUUID()));
        loanRequest.set_client_type(getClientType(httpRequest));
        logger.debug("ApiControllerImpl.java inside listLoans() Request : {}", loanRequest);
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            logger.debug("ApiControllerImpl.java inside listLoans() ADMIN role verified.");
            tempResponse = loanService.getAllLoans(loanRequest, httpRequest);
        } else {
            logger.debug("ApiControllerImpl.java inside listLoans() Not - ADMIN role.");
            String email = userDetails.getUsername();
            tempResponse = loanService.getLoansByCustomerId(email, loanRequest);
        }

        if (loanRequest.getData().getParamData().get(0).getFormat().equalsIgnoreCase("xml")) {
            httpServletResponse.addHeader("Accept", "application/xml");
            logger.debug("ApiControllerImpl.java inside listLoans() format type 'xml' verified.");
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(tempResponse);
        } else {
            return tempResponse;
        }

    }

    // 8. API endpoint: api/update_loans_status
    // localhost:8080/api/update_loans_status
    @PostMapping("/update_loans_status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateLoanStatus(@RequestBody ApiRequest apiRequest, HttpServletRequest httpRequest) {
        apiRequest.set_client_ts(new Timestamp(System.currentTimeMillis()));
        apiRequest.set_req_id(String.valueOf(UUID.randomUUID()));
        apiRequest.set_client_type(getClientType(httpRequest));
        logger.debug("ApiControllerImpl.java inside updateLoanStatus() Request : {}", apiRequest);
        return loanService.updateLoanStatus(apiRequest);
    }

    private String getClientType(HttpServletRequest httpServletRequest) {
        // Get the user agent header from HttpServletRequest object
        String userAgent = httpServletRequest.getHeader("User-Agent");
        logger.debug("ApiControllerImpl.java inside getClientType() Request : {}", userAgent);
        if (userAgent.contains("Mozilla")) {
            return "Web";
        } else if (userAgent.contains("iOS")) {
            return "iOS";
        } else if (userAgent.contains("Android")) {
            return "Android";
        } else {
            return "local";
        }
    }

}
