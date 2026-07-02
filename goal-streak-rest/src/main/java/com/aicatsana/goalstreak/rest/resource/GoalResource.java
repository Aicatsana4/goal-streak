package com.aicatsana.goalstreak.rest.resource;

import com.aicatsana.goalstreak.domain.model.Goal;
import com.aicatsana.goalstreak.rest.dto.RequestGoalDto;
import com.aicatsana.goalstreak.rest.dto.ResponseGoalDto;
import com.aicatsana.goalstreak.rest.mapper.GoalMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class GoalResource {

    @PostMapping("/rest/goals")
    public ResponseEntity<ResponseGoalDto> createGoal(@RequestBody RequestGoalDto requestGoalDto){
        Goal goal = GoalMapper.toGoal(requestGoalDto);

        return ResponseEntity.ok(GoalMapper.toResponseGoalDto(goal));
    }
}
