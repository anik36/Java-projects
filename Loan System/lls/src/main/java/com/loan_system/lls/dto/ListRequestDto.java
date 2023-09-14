package com.loan_system.lls.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListRequestDto {

    private String columns;
    private int limit;
    private int offset;
    private String format;
    private String requested_date;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Timestamp created_at;
    private String loan_type_id;
    private String status;
}
