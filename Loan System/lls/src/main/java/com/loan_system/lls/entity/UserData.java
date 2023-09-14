package com.loan_system.lls.entity;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.loan_system.lls.audit.BaseEntity;
import com.loan_system.lls.helper.Gender;
import com.loan_system.lls.helper.validation.ValidDateOfBirth;
import com.loan_system.lls.helper.validation.ValidMobile;
import com.loan_system.lls.token.Token;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "_user")
public class UserData extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_generator")
    @SequenceGenerator(name = "id_generator", allocationSize = 2)
    private Integer user_id;
    @Pattern(regexp = "(^[a-zA-Z ]*$)", message = "Only characters and space allowed in firstName")
    private String fname;
    @Pattern(regexp = "(^[a-zA-Z ]*$)", message = "Only characters and space allowed in middleName")
    private String mname;
    @Pattern(regexp = "(^[a-zA-Z ]*$)", message = "Only characters and space allowed in lastName")
    private String lname;
    @NotBlank
    @Email(message = "enter valid email address")
    @Column(unique = true)
    private String email;
    // @Pattern(regexp = "(^.{5,}$)", message = "minimum 5 character allowed.")
    private String password;
    @Past(message = "date of birth must be in the past")
    @ValidDateOfBirth
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dob;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String role;
    @Pattern(regexp = "(^[0-9]{10}$)", message = "mobile not valid.")
    @ValidMobile
    @Column(unique = true)
    private String mobile;
    private String ip;
    private boolean is_verified;
    private String occupation;
    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "user_id")
    private Address address;
    private Timestamp last_login_at;

    // @OneToMany(mappedBy = "user")
    // private List<Token> tokens;

    public UserData(
            @Pattern(regexp = "(^[a-zA-Z ]*$)", message = "Only characters and space allowed in firstName") String fname,
            @Pattern(regexp = "(^[a-zA-Z ]*$)", message = "Only characters and space allowed in middleName") String mname,
            @Pattern(regexp = "(^[a-zA-Z ]*$)", message = "Only characters and space allowed in lastName") String lname,
            @NotBlank @Email(message = "enter valid email address") String email, String password,
            @Past(message = "date of birth must be in the past") Date dob, Gender gender, String role,
            @Pattern(regexp = "(^[0-9]{10}$)", message = "mobile not valid.") String mobile, String ip,
            String occupation, Address address, Timestamp last_login_at, boolean is_verified) {
        this.fname = fname;
        this.mname = mname;
        this.lname = lname;
        this.email = email;
        this.password = password;
        this.dob = dob;
        this.gender = gender;
        this.role = role;
        this.mobile = mobile;
        this.ip = ip;
        this.is_verified = is_verified;
        this.occupation = occupation;
        this.address = address;
        this.last_login_at = last_login_at;
    }

    public boolean getIs_verified() {
        return is_verified;
    }

    public void setIs_verified(boolean is_verified) {
        this.is_verified = is_verified;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
