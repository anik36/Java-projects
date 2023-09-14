package com.loan_system.lls.helper;

import java.sql.Timestamp;

import com.loan_system.lls.dto.AddUserDataDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddUserRequest {
    private AddUserDataDto data;
    private String _req_id;
    private Timestamp _client_ts;
    private String _client_type;
}
