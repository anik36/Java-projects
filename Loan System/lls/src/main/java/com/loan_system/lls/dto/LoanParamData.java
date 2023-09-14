package com.loan_system.lls.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor 
public class LoanParamData {
    private List<String> fields;
    private Integer limit;
    private Integer offset;
    private String format;
    private Map<String, String> sorting;
    private Map<String, List<String>> filterParams;

    public String getField(int index) {
        if (fields == null || index < 0 || index >= fields.size()) {
            return null;
        }
        return fields.get(index);
    }
}

