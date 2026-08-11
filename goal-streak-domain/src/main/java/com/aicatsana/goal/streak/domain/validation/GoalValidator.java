package com.aicatsana.goal.streak.domain.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class GoalValidator implements ConstraintValidator<ValidGoal, String> {

    private static final Pattern PRINTABLE_PATTERN = Pattern.compile("^\\p{Print}{3,20}$");

    @Override
    public boolean isValid(String goalName, ConstraintValidatorContext context) {
        if (goalName == null || goalName.isBlank()) {
            return false;
        }
        return PRINTABLE_PATTERN.matcher(goalName).matches();
    }
}
