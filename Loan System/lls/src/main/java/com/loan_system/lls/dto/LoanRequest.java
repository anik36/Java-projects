package com.loan_system.lls.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequest {
    private List<Integer> id;
    private List<LoanParamData> paramData;

}
