package com.aicatsana.goal.streak.infra.file;

import com.aicatsana.goal.streak.domain.model.Goal;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GoalFileReader {

    private final File file;
    private final ObjectMapper mapper;

    public GoalFileReader(GoalFileReaderProperties properties) throws IOException {
        this.file = new File(properties.goalsFilePath());
        this.mapper = new ObjectMapper().configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        initializeFile();
    }

    public void write(Goal goal) throws IOException {
        if (goal == null || goal.goalDurationInDays() <= 0 || isExistingGoalName(goal)) {
            return;
        }

        Set<Goal> goals = readGoals();
        goals.add(goal);
        mapper.writeValue(file, goals);
    }

    public Optional<Set<Goal>> readAll() throws IOException {
        if (!file.exists()) {
            return Optional.empty();
        }

        return Optional.of(readGoals());
    }

    public Optional<Set<Goal>> readByGoalName(String goalName) throws IOException {
        if (!file.exists()) {
            return Optional.empty();
        }

        Set<Goal> goals = readGoals();
        Set<Goal> filteredGoals = new HashSet<>();
        for (Goal goal : goals) {
            if (goal.goalName().equals(goalName)) {
                filteredGoals.add(goal);
            }
        }
        return Optional.of(filteredGoals);
    }

    public void updateByGoalName(String goalName, Goal newGoal) throws IOException {
        if (!file.exists() || newGoal == null || newGoal.goalDurationInDays() <= 0) {
            return;
        }

        Set<Goal> goals = readGoals();
        boolean updated = false;
        for (Goal goal : goals) {
            if (goal.goalName().equals(goalName)) {
                goals.remove(goal);
                goals.add(new Goal(newGoal.goalName(), newGoal.goalDurationInDays()));
                updated = true;
                break;
            }
        }

        if (updated) {
            mapper.writeValue(file, goals);
        }
    }

    public void updateGoalDurationInDays(String goalName, int goalDurationInDays) throws IOException {
        if (!file.exists() || goalDurationInDays <= 0) {
            return;
        }

        Set<Goal> goals = readGoals();
        boolean updated = false;
        for (Goal goal : goals) {
            if (goal.goalName().equals(goalName)) {
                goals.remove(goal);
                goals.add(new Goal(goalName, goalDurationInDays));
                updated = true;
                break;
            }
        }

        if (updated) {
            mapper.writeValue(file, goals);
        }
    }

    public void deleteByGoalName(String goalName) throws IOException {
        if (file.exists()) {
            Set<Goal> goals = readGoals();
            boolean removed = goals.removeIf(goal -> goal.goalName().equals(goalName));
            if (removed) {
                mapper.writeValue(file, goals);
            }
        }
    }

    private void initializeFile() throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        if (!file.exists() || file.length() == 0) {
            mapper.writeValue(file, new HashSet<Goal>());
        }
    }

    private Set<Goal> readGoals() throws IOException {
        if (!file.exists() || file.length() == 0) {
            return new HashSet<>();
        }
        return mapper.readValue(file, new TypeReference<>() {
        });
    }

    private boolean isExistingGoalName(Goal goal) throws IOException {
        return readGoals().stream().anyMatch(g -> g.goalName().equals(goal.goalName()));
    }
}
