package com.loan_system.lls.helper.validation;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MobileValidator implements ConstraintValidator<ValidMobile, String> {
    @Override
    public void initialize(ValidMobile constraintAnnotation) {
    }

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        // creating an instance of PhoneNumber Utility class
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        // creating a variable of type PhoneNumber
        PhoneNumber phoneNumber = null;
        try {
            // the parse method parses the string and returns a PhoneNumber in the format of
            // specified region
            phoneNumber = phoneUtil.parse(phone, "IN");

            // this statement prints the type of the phone number
            System.out.println("Type: " + phoneUtil.getNumberType(phoneNumber));
        } catch (NumberParseException e) {

            // if the phoneUtil is unable to parse any phone number an exception
            // occurs and gets caught in this block
            System.out.println("Unable to parse the given phone number: " + phone);
            // e.printStackTrace();
        }
        if (phoneNumber == null) {
            System.out.println("Null phone number: " + phone);
            return false;
        }
        // return the boolean value of the validation performed
        return phoneUtil.isValidNumber(phoneNumber);
    }

}
