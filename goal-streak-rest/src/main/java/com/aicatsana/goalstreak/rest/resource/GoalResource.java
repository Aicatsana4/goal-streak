package com.aicatsana.goalstreak.rest.resource;

import com.aicatsana.goalstreak.rest.dto.RequestGoalDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class GoalResource {

    @PostMapping("/rest/goals")
    public ResponseEntity<?> createGoal(@RequestBody RequestGoalDto requestGoalDto){
        return ResponseEntity.ok(requestGoalDto);
    }
}
