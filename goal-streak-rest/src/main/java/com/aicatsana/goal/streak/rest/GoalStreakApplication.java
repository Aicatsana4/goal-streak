package com.aicatsana.goal.streak.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.aicatsana.goal.streak")
public class GoalStreakApplication {

	static void main() {
		SpringApplication.run(GoalStreakApplication.class);
	}

}
