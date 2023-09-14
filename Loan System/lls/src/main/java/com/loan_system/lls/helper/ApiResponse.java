package com.loan_system.lls.helper;


import java.sql.Timestamp;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private String status = "ERROR";
    private int status_code;
    private String status_msg;
    private Object data;
    private String  _req_id;
    private Timestamp _server_ts;

}
