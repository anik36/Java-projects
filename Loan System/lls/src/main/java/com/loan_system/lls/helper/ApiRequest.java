package com.loan_system.lls.helper;

import java.sql.Timestamp;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ApiRequest {
    private Map<String,String> data;
    private String _req_id;
    private Timestamp _client_ts;
    private String _client_type;
}
