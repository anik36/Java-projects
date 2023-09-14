package com.loan_system.lls.helper.validation;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateOfBirthValidator implements ConstraintValidator<ValidDateOfBirth, Date> {
    @Override
    public void initialize(ValidDateOfBirth constraintAnnotation) {
    }

    @Override
    public boolean isValid(Date dateOfBirth, ConstraintValidatorContext context) {
        if (dateOfBirth == null) {
            return true;
        }
        LocalDate currentDate = LocalDate.now();
        LocalDate dob = dateOfBirth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate minDateOfBirth = currentDate.minusYears(100);
        return dob.isBefore(currentDate) && dob.isAfter(minDateOfBirth);
    }
}
