package com.loan_system.lls.helper.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MobileValidator.class)
public @interface ValidMobile {

    String message() default "Invalid mobile number!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
