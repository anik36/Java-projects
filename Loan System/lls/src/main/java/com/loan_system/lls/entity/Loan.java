package com.loan_system.lls.entity;

import java.util.Date;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.loan_system.lls.audit.BaseEntity;
import com.loan_system.lls.helper.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Builder
// @EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "loan_system")
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class Loan extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_generator")
    @SequenceGenerator(name = "id_generator", allocationSize = 2)
    @Column(name = "loan_id")
    private int loan_id;

    @JsonIgnore
    @NotNull(message = "please enter loan_type_id")
    private int loan_type_id;

    // @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_type_id", insertable = false, updatable = false)
    private LoanType loanType;

    private Date requested_date;

    private double principle;

    private double rate;
    private int time;
    private double simple_interest;
    private double amount;
    @Enumerated(EnumType.STRING)
    private Status status = Status.pending;

    @NotNull(message = "please enter user_id")
    private int user_id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserData userData;

    public Loan(int user_id, int loan_type_id, double principle, double rate, int time) {
        this.user_id = user_id;
        this.loan_type_id = loan_type_id;
        this.principle = principle;
        this.rate = rate;
        this.time = time;
        this.status = Status.pending;
        this.requested_date = new Date();
        this.simple_interest = (principle * rate * time) / 100;
        this.amount = simple_interest + principle;
    }


}
