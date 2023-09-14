package com.loan_system.lls.exception;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.loan_system.lls.helper.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private ApiResponse response;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, Object> data = new HashMap<>();
        List<String> errors = new ArrayList<>();

        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.add(fieldError.getDefaultMessage());
        }

        for (ObjectError objectError : exception.getBindingResult().getGlobalErrors()) {
            errors.add(objectError.getDefaultMessage());
        }
        data.put("errors", errors);
        // setting response
        response.setStatus("error");
        response.setStatus_code(HttpStatus.BAD_REQUEST.value());
        response.setStatus_msg("validation failed.");
        response.setData(Arrays.toString(errors.toArray()));
        response.set_server_ts(new Timestamp(System.currentTimeMillis()));
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
            HttpServletRequest request) {
        String[] message;
        // Get the request path or URL
        String requestPath = request.getRequestURI();
        if ("/api/add_customer".equals(requestPath)) {
            message = new String[] { ex.getMessage() };
        } else if ("/api/request_loan".equals(requestPath)) {
            message = new String[] { "loan_type_id not present in database" };
        } else {
            message = new String[] { "data integrity violation." };
        }
        // setting response
        response.setStatus("error");
        response.setStatus_code(HttpStatus.BAD_REQUEST.value());
        response.setStatus_msg("data integrity violation.");
        response.setData(Arrays.toString(message));
        response.set_server_ts(new Timestamp(System.currentTimeMillis()));

        return ResponseEntity.badRequest().body(response);
    }
    

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException ex,
            HttpServletRequest request) {
        String message;
        // Get the request path or URL
        String requestPath = request.getRequestURI();
        if ("/api/update_loans_status".equals(requestPath)) {
            message = "only 'pending' or 'approved' or 'rejected' allowed" ;
        // } else if ("/api/request_loan".equals(requestPath)) {
        //     message = new String[] { "loan_type_id not present in database" };
        } else {
            message = "data integrity violation.";
        }
        // setting response
        response.setStatus("error");
        response.setStatus_code(HttpStatus.BAD_REQUEST.value());
        response.setStatus_msg("data integrity violation.");
        response.setData(message);
        response.set_server_ts(new Timestamp(System.currentTimeMillis()));

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> getHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String[] message ;
        // Get the request path or URL
        String requestPath = request.getRequestURI();
        if ("/api/add_customer".equals(requestPath)) {
            message = new String[] { ex.getMessage() };
        } else if ("/api/import_loan_types".equals(requestPath)) {
            message = new String[] { "enter data in proper JSON format." };
        } else {
            message = new String[] { "data integrity violation also please check API Url is correct!" };
        }
        // setting response
        response.setStatus("error");
        response.setStatus_code(HttpStatus.BAD_REQUEST.value());
        response.setStatus_msg("input data format violation.");
        response.setData(Arrays.toString(message));
        response.set_server_ts(new Timestamp(System.currentTimeMillis()));
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = { NullPointerException.class })
    public ResponseEntity<?> handleNullPointerException(
            NullPointerException ex, HttpServletRequest request) {
        // Return an appropriate error message to the client
        String message ;
        // Get the request path or URL
        String requestPath = request.getRequestURI();
        if ("/api/login".equals(requestPath)) {
            message = "email is not registered with us";
        // } else if ("/api/import_loan_types".equals(requestPath)) {
        //     message = new String[] { "Enter data in proper JSON format." };
        } else {
            message = "no record with null value";
        }
        response.setStatus("error");
        response.setStatus_code(HttpStatus.BAD_REQUEST.value());
        response.setStatus_msg(message);
        // response.setData(message);
        response.set_server_ts(new Timestamp(System.currentTimeMillis()));
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleConstraintViolationException(
            HttpServletRequest request, ConstraintViolationException ex) {
        Map<String, Object> errors = new HashMap<>();
        response.setStatus("error");
        response.setStatus_code(HttpStatus.BAD_REQUEST.value());
        response.setStatus_msg("data integrity violation.");
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        response.setData(errors);
        response.set_server_ts(new Timestamp(System.currentTimeMillis()));
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(value = { NumberFormatException.class })
    public ResponseEntity<?> handleNumberFormatException(
        NumberFormatException ex, HttpServletRequest request) {
        // Return an appropriate error message to the client
        String message = "Enter numbers only ";
        response.setStatus("error");
        response.setStatus_code(HttpStatus.BAD_REQUEST.value());
        response.setStatus_msg("a NumberFormatException occurred.");
        response.setData(message);
        response.set_server_ts(new Timestamp(System.currentTimeMillis()));
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(value = { NoSuchElementException.class })
    public ResponseEntity<ApiResponse> handleNoSuchElementException(NoSuchElementException ex, HttpServletRequest request) {
        String[] message ;
        // Get the request path or URL
        String requestPath = request.getRequestURI();
        if ("/api/request_loan".equals(requestPath)) {
            message = new String[] { "customer_id not found in Database" };
        } else {
            message = new String[] { "no such_element_exception found." };
        }
        // setting response
        response.setStatus("error");
        response.setStatus_code(HttpStatus.BAD_REQUEST.value());
        response.setStatus_msg("invalid data input");
        response.setData(Arrays.toString(message));
        response.set_server_ts(new Timestamp(System.currentTimeMillis()));
        return ResponseEntity.badRequest().body(response);
    }
    

    // @ExceptionHandler(value = { UsernameNotFoundException.class })
    // public ResponseEntity<ApiResponse> handleUsernameNotFoundException(UsernameNotFoundException ex, HttpServletRequest request) {
    //     String[] message ;
    //     // Get the request path or URL
    //     String requestPath = request.getRequestURI();
    //     if ("/api/login".equals(requestPath)) {
    //         message = new String[] { ex.getLocalizedMessage() };
    //     } else {
    //         message = new String[] { "A No Such user found." };
    //     }
    //     // setting response
    //     response.setStatus("error");
    //     response.setStatus_code(HttpStatus.BAD_REQUEST.value());
    //     response.setStatus_msg("Invalied data input");
    //     response.setData(Arrays.toString(message));
    //     response.set_server_ts(new Timestamp(System.currentTimeMillis()));
    //     return ResponseEntity.badRequest().body(response);
    // }
}
