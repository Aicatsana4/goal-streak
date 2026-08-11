package com.aicatsana.goal.streak.domain.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.util.Set;

public class ValidationUtil {

    private static final Validator VALIDATOR = Validation.byProvider(HibernateValidator.class)
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator()) // Avoids needing jakarta.el
            .buildValidatorFactory()
            .getValidator();

    private ValidationUtil() {
        // default private constructor to prevent instantiation
    }

    public static void validate(Object object) {
        Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(object);
        violations.stream().findFirst().ifPresent(v -> {
            String path = (v.getLeafBean().getClass().getSimpleName() + "." + v.getPropertyPath().toString());
            throw new ValidationException(path + " " + v.getMessage());
        });
    }
}
