package com.aicatsana.goal.streak.infra.exception;

public class GoalAlreadyExistsException extends RuntimeException {
    public GoalAlreadyExistsException(String message) {
        super(message);
    }
}
