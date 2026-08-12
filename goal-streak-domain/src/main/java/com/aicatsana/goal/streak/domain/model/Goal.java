package com.aicatsana.goal.streak.domain.model;

import com.aicatsana.goal.streak.domain.validation.ValidGoal;
import com.aicatsana.goal.streak.domain.validation.ValidationUtil;
import jakarta.validation.constraints.PositiveOrZero;

public record Goal(@ValidGoal
                   String goalName,
                   @PositiveOrZero
                   int goalDurationInDays) {

    public Goal(String goalName, int goalDurationInDays) {
        this.goalName = goalName;
        this.goalDurationInDays = goalDurationInDays;

        ValidationUtil.validate(this);
    }
}
