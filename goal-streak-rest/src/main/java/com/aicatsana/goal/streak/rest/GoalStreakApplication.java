package com.aicatsana.goal.streak.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.aicatsana.goal.streak")
public class GoalStreakApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoalStreakApplication.class, args);
	}

}
