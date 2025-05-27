package com.fidypay.utils.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author prave
 * @Date 09-10-2023
 */
public class UppercaseValidator implements ConstraintValidator<Uppercase, String> {

    @Override
    public void initialize(Uppercase constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && !value.trim().isEmpty() && value.equals(value.toUpperCase());
    }
}
