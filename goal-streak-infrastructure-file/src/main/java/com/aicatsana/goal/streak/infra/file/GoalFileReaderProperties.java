package com.aicatsana.goal.streak.infra.file;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "goal-file-reader")
public record GoalFileReaderProperties(String goalsFilePath){
}
