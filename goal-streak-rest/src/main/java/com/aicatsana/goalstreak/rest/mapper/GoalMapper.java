package com.aicatsana.goalstreak.rest.mapper;

import com.aicatsana.goalstreak.domain.model.Goal;
import com.aicatsana.goalstreak.rest.dto.RequestGoalDto;
import com.aicatsana.goalstreak.rest.dto.ResponseGoalDto;

public class GoalMapper {

    public static Goal toGoal(ResponseGoalDto responseGoalDto) {
        return new Goal(responseGoalDto.goalTitle(), responseGoalDto.goalDurationDays());
    }

    public static Goal toGoal(RequestGoalDto requestGoalDto) {
        return new Goal(requestGoalDto.goalTitle(), requestGoalDto.goalDurationDays());
    }

    public static RequestGoalDto toRequestGoalDto(Goal goal) {
        return new RequestGoalDto(goal.goalTitle(), goal.goalDurationDays());
    }

    public static ResponseGoalDto toResponseGoalDto(Goal goal) {
        return new ResponseGoalDto(goal.goalTitle(), goal.goalDurationDays());
    }
}
