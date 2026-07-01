package com.aicatsana.goalstreak.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat
public record ResponseGoalDto(String goalTitle,
                              int goalDurationDays) {
}
