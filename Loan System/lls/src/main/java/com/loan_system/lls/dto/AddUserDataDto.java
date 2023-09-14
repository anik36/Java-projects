package com.loan_system.lls.dto;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.loan_system.lls.entity.Address;
import com.loan_system.lls.helper.Gender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddUserDataDto {

    private String fname;
    private String mname;
    private String lname;
    private String email;
    private String password;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dob;
    private Gender gender;
    private String role;
    private String mobile;
    private String ip;
    private boolean is_verified;
    private String occupation;
    private Address address;
    private Timestamp last_login_at;

    public boolean getIs_verified() {
        return is_verified;
    }

    public void setIs_verified(boolean is_verified) {
        this.is_verified = is_verified;
    }

}
