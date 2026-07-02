package com.aicatsana.goal.streak.domain.model;

import java.util.Objects;

public class Goal {
    private String goalName;
    private int goalDurationInDays;

    public Goal() {
    }

    public Goal(String goalName, int goalDurationInDays) {
        this.goalName = goalName;
        this.goalDurationInDays = goalDurationInDays;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public int getGoalDurationInDays() {
        return goalDurationInDays;
    }

    public void setGoalDurationInDays(int goalDurationInDays) {
        this.goalDurationInDays = goalDurationInDays;
    }

    @Override
    public String toString() {
        return "{" +
                "\"goalName\":\"" + goalName + "\"," +
                "\"goalDurationInDays\":" + goalDurationInDays +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return goalDurationInDays == goal.goalDurationInDays && Objects.equals(goalName, goal.goalName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(goalName, goalDurationInDays);
    }
}
