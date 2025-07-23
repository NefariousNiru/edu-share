package com.nefarious.edu_share.shared.utils;
import jakarta.mail.internet.InternetAddress;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;

public class Validators {
    public static void assertValidEmail(String email, String objectName) {
        // Asset if email is valid else throw a validation error
        try {
            InternetAddress address = new InternetAddress(email);
            address.validate();
        } catch (Exception ex) {
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(email, objectName);
            bindingResult.addError(new FieldError(objectName, "email", "Invalid email format"));
            throw new WebExchangeBindException(null, bindingResult);
        }
    }
}
