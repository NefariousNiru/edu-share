package com.nefarious.edu_share.user.validator;

import com.nefarious.edu_share.user.annotation.MinimumAge;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class MinimumAgeValidator implements ConstraintValidator<MinimumAge, LocalDate> {
    private int minimumAge;

    @Override
    public void initialize(MinimumAge constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.minimumAge = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate == null) return false;
        return !localDate.isAfter(LocalDate.now().minusYears(minimumAge));
    }
}
