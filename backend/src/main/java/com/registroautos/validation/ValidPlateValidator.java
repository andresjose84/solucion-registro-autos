package com.registroautos.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidPlateValidator implements ConstraintValidator<ValidPlate, String> {

    private static final String PLATE_PATTERN = "^[A-Z]{3}\\d{3}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return value.trim().toUpperCase().matches(PLATE_PATTERN);
    }
}
