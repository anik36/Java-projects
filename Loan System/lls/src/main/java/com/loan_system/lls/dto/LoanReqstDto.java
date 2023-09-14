package com.loan_system.lls.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class LoanReqstDto {

    @JsonInclude(Include.NON_NULL)
    private Integer loan_id;

    @JsonInclude(Include.NON_NULL)
    private Integer loan_type_id;

    @JsonInclude(Include.NON_NULL)
    private String loanType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonInclude(Include.NON_NULL)
    private Timestamp requested_date;

    @JsonInclude(Include.NON_NULL)
    private Double principle;

    @JsonInclude(Include.NON_NULL)
    private Double rate;

    @JsonInclude(Include.NON_NULL)
    private Integer time;

    @JsonInclude(Include.NON_NULL)
    private Double simple_interest;

    @JsonInclude(Include.NON_NULL)
    private Double amount;

    @JsonInclude(Include.NON_NULL)
    private String status;

    @JsonInclude(Include.NON_NULL)
    private Integer user_id;
}
