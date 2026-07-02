package com.aicatsana.goal.streak.infra.file;

import com.aicatsana.goal.streak.domain.model.Goal;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GoalFileReaderTest {

    @TempDir
    private Path tempDir;
    private Path file;
    private GoalFileReader goalFileReader;
    private ObjectMapper mapper;

    @BeforeEach
    void createFile() throws IOException {
        this.tempDir = Files.createTempDirectory("goal-test-");
        this.file = tempDir.resolve("goals.json");
        this.mapper = new ObjectMapper();
        GoalFileReaderProperties goalFileReaderProperties = new GoalFileReaderProperties(file.toString());
        this.goalFileReader = new GoalFileReader(goalFileReaderProperties);
    }

    @AfterEach
    void checkTempDir() {
        try {
            Files.delete(file);
            Files.delete(tempDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    void write_validIntoEmptyFile_successful() throws IOException {
        // prepare
        Goal actualGoal = new Goal("my goal", 20);

        // test
        goalFileReader.write(actualGoal);

        // assert
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {});

        assertThat(content).satisfiesExactly(
                goal -> {
                    assertThat(goal.getGoalName()).isEqualTo(actualGoal.getGoalName());
                    assertThat(goal.getGoalDurationInDays()).isEqualTo(actualGoal.getGoalDurationInDays());
                }
        );
    }

    @Test
    void write_valid_successful() throws IOException {
        // prepare
        Goal actualGoal1 = new Goal("my goal 1", 10);
        Goal actualGoal2 = new Goal("my goal 2", 20);
        mapper.writeValue(file.toFile(), Set.of(actualGoal1));

        // test
        goalFileReader.write(actualGoal2);

        // assert
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {});

        assertThat(content).satisfiesExactlyInAnyOrder(
                goal -> {
                    assertThat(goal.getGoalName()).isEqualTo(actualGoal1.getGoalName());
                    assertThat(goal.getGoalDurationInDays()).isEqualTo(actualGoal1.getGoalDurationInDays());
                },
                goal -> {
                    assertThat(goal.getGoalName()).isEqualTo(actualGoal2.getGoalName());
                    assertThat(goal.getGoalDurationInDays()).isEqualTo(actualGoal2.getGoalDurationInDays());
                }
        );
    }

    @Test
    void write_alreadyExistingGoalName_noChange() throws IOException {
        // prepare
        Goal actualGoal1 = new Goal("my goal 1", 10);
        Goal actualGoalwithExistingName = new Goal("my goal 1", 20);
        mapper.writeValue(file.toFile(), Set.of(actualGoal1));

        // test
        goalFileReader.write(actualGoalwithExistingName);

        // assert
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {});

        assertThat(content).satisfiesExactly(
                goal -> {
                    assertThat(goal.getGoalName()).isEqualTo(actualGoal1.getGoalName());
                    assertThat(goal.getGoalDurationInDays()).isEqualTo(actualGoal1.getGoalDurationInDays());
                }
        ).hasSize(1);
    }

    @Test
    void write_negativeDurationIntoEmptyFile_failed() throws IOException {
        // prepare
        Goal actualGoalWithNegativeDuration = new Goal("my goal", -1);

        // test
        goalFileReader.write(actualGoalWithNegativeDuration);

        // assert
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {});
        assertThat(content).isEmpty();
    }

    @Test
    void write_negativeDuration_failed() throws IOException {
        // prepare
        Goal actualGoal = new Goal("my goal 1", 1);
        Goal actualGoalWithNegativeDuration = new Goal("my goal 2", -1);
        mapper.writeValue(file.toFile(), Set.of(actualGoal));

        // test
        goalFileReader.write(actualGoalWithNegativeDuration);

        // assert
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {});
        assertThat(content).isNotEmpty().satisfiesExactly(
                goal -> {
                    assertThat(goal.getGoalName()).isEqualTo(actualGoal.getGoalName());
                    assertThat(goal.getGoalDurationInDays()).isEqualTo(actualGoal.getGoalDurationInDays());
                }
        );

    }

    @Test
    void readGoals_valid_successful() throws IOException{
        // prepare
        Goal actualGoal1 = new Goal("my goal 1", 20);
        Goal actualGoal2 = new Goal("my goal 2", 30);
        mapper.writeValue(file.toFile(), Set.of(actualGoal1, actualGoal2));

        // test
        Set<Goal> readContent = goalFileReader.readAll().orElse(Collections.emptySet());

        // assert
        assertThat(readContent).satisfiesExactlyInAnyOrder(
                goal -> {
                    assertThat(goal.getGoalName()).isEqualTo(actualGoal1.getGoalName());
                    assertThat(goal.getGoalDurationInDays()).isEqualTo(actualGoal1.getGoalDurationInDays());
                },
                goal -> {
                    assertThat(goal.getGoalName()).isEqualTo(actualGoal2.getGoalName());
                    assertThat(goal.getGoalDurationInDays()).isEqualTo(actualGoal2.getGoalDurationInDays());
                }
        );
    }

    @Test
    void readByGoalName_valid_successful() throws IOException{
        // prepare
        Goal actualGoal1 = new Goal("my goal 1", 20);
        Goal actualGoal2 = new Goal("my goal 2", 30);
        mapper.writeValue(file.toFile(), Set.of(actualGoal1, actualGoal2));

        // test
        Set<Goal> readContent = goalFileReader.readByGoalName(actualGoal1.getGoalName()).orElse(Collections.emptySet());

        // assert
        assertThat(readContent).satisfiesExactly(
                goal -> {
                    assertThat(goal.getGoalName()).isEqualTo(actualGoal1.getGoalName());
                    assertThat(goal.getGoalDurationInDays()).isEqualTo(actualGoal1.getGoalDurationInDays());
                }
        );
    }

    @Test
    void readByGoalName_goalNameDoesNotExist_successful() throws IOException{
        // prepare
        Goal actualGoal1 = new Goal("my goal 1", 20);
        Goal actualGoal2 = new Goal("my goal 2", 30);
        mapper.writeValue(file.toFile(), Set.of(actualGoal1, actualGoal2));

        // test
        Set<Goal> readContent = goalFileReader.readByGoalName("non-existent goal").orElse(Collections.emptySet());

        // assert
        assertThat(readContent).isEmpty();
    }

    @Test
    void updateByGoalName_validWithSingleGoal_successful() throws IOException {
        // prepare
        Goal actualGoal = new Goal("my goal", 20);
        Goal updatedGoal = new Goal("my updated goal", 30);
        mapper.writeValue(file.toFile(), Set.of(actualGoal));

        // test
        goalFileReader.updateByGoalName(actualGoal.getGoalName(), updatedGoal);

        // assert
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {});
        assertThat(content).satisfiesExactly(
                goal -> {
                    assertThat(goal.getGoalName()).isEqualTo(updatedGoal.getGoalName());
                    assertThat(goal.getGoalDurationInDays()).isEqualTo(updatedGoal.getGoalDurationInDays());
                }
        );
    }

    @Test
    void updateByGoalName_valid_successful() throws IOException {
        // prepare
        Goal actualGoal1 = new Goal("my goal 1", 20);
        Goal actualGoal2 = new Goal("my goal 2", 30);
        Goal updatedGoal = new Goal("my updated goal", 40);
        mapper.writeValue(file.toFile(), Set.of(actualGoal1, actualGoal2));

        // test
        goalFileReader.updateByGoalName(actualGoal1.getGoalName(), updatedGoal);

        // assert
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {});
        assertThat(content).satisfiesExactlyInAnyOrder(
                goal -> {
                    assertThat(goal.getGoalName()).isEqualTo(updatedGoal.getGoalName());
                    assertThat(goal.getGoalDurationInDays()).isEqualTo(updatedGoal.getGoalDurationInDays());
                },
                goal -> {
                    assertThat(goal.getGoalName()).isEqualTo(actualGoal2.getGoalName());
                    assertThat(goal.getGoalDurationInDays()).isEqualTo(actualGoal2.getGoalDurationInDays());
                }
        );
    }

    @Test
    void updateByGoalName_negativeDurationWithSingleGoal_failed() throws IOException {
        // prepare
        Goal actualGoal = new Goal("my goal", 20);
        Goal updatedGoalWithNegativeDuration = new Goal("my updated goal", -1);
        mapper.writeValue(file.toFile(), Set.of(actualGoal));

        // test
        goalFileReader.updateByGoalName(actualGoal.getGoalName(), updatedGoalWithNegativeDuration);

        // assert
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {});
        assertThat(content).satisfiesExactly(
                goal -> {
                    assertThat(goal.getGoalName()).isNotEqualTo(updatedGoalWithNegativeDuration.getGoalName());
                    assertThat(goal.getGoalDurationInDays()).isEqualTo(actualGoal.getGoalDurationInDays());
                }
        );
    }

    @Test
    void updateByGoalName_negativeDuration_failed() throws IOException {
        // prepare
        Goal actualGoal1 = new Goal("my goal 1", 20);
        Goal actualGoal2 = new Goal("my goal 2", 30);
        Goal updatedGoalWithNegativeDuration = new Goal("my updated goal", -1);
        mapper.writeValue(file.toFile(), Set.of(actualGoal1, actualGoal2));

        // test
        goalFileReader.updateByGoalName(actualGoal1.getGoalName(), updatedGoalWithNegativeDuration);

        // assert
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {});
        assertThat(content).satisfiesExactlyInAnyOrder(
                goal -> {
                    assertThat(goal.getGoalName()).isNotEqualTo(updatedGoalWithNegativeDuration.getGoalName());
                    assertThat(goal.getGoalName()).isEqualTo(actualGoal1.getGoalName());
                    assertThat(goal.getGoalDurationInDays()).isEqualTo(actualGoal1.getGoalDurationInDays());
                },
                goal -> {
                    assertThat(goal.getGoalName()).isEqualTo(actualGoal2.getGoalName());
                    assertThat(goal.getGoalDurationInDays()).isEqualTo(actualGoal2.getGoalDurationInDays());
                }
        );
    }

    @Test
    void deleteByGoalName_validWithSingleGoal_successful() throws IOException {
        // prepare
        Goal actualGoal = new Goal("my goal", 20);
        mapper.writeValue(file.toFile(), Set.of(actualGoal));

        // test
        goalFileReader.deleteByGoalName(actualGoal.getGoalName());

        // assert
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {});
        assertThat(content).isEmpty();
    }

    @Test
    void deleteByGoalName_valid_successful() throws IOException {
        // prepare
        Goal actualGoal1 = new Goal("my goal 1", 20);
        Goal actualGoal2 = new Goal("my goal 2", 30);
        mapper.writeValue(file.toFile(), Set.of(actualGoal1, actualGoal2));

        // test
        goalFileReader.deleteByGoalName(actualGoal1.getGoalName());

        // assert
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {});
        assertThat(content).satisfiesExactly(
                goal -> {
                    assertThat(goal.getGoalName()).isEqualTo(actualGoal2.getGoalName());
                    assertThat(goal.getGoalDurationInDays()).isEqualTo(actualGoal2.getGoalDurationInDays());
                }
        ).size().isEqualTo(1);
    }

    @Test
    void deleteByGoalName_validFromEmptyFile_successful() throws IOException {
        // prepare
        Goal actualGoal1 = new Goal("my goal 1", 20);

        // test
        goalFileReader.deleteByGoalName(actualGoal1.getGoalName());

        // assert
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {});
        assertThat(content).isEmpty();
    }

    @Test
    void deleteByGoalName_nonExistentGoal_noChange() throws IOException {
        // prepare
        Goal actualGoal1 = new Goal("my goal 1", 20);
        Goal actualGoal2 = new Goal("my goal 2", 30);
        mapper.writeValue(file.toFile(), Set.of(actualGoal1, actualGoal2));

        // test
        goalFileReader.deleteByGoalName("non-existent goal");

        // assert
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {});
        assertThat(content).satisfiesExactlyInAnyOrder(
                goal -> {
                    assertThat(goal.getGoalName()).isEqualTo(actualGoal1.getGoalName());
                    assertThat(goal.getGoalDurationInDays()).isEqualTo(actualGoal1.getGoalDurationInDays());
                },
                goal -> {
                    assertThat(goal.getGoalName()).isEqualTo(actualGoal2.getGoalName());
                    assertThat(goal.getGoalDurationInDays()).isEqualTo(actualGoal2.getGoalDurationInDays());
                }
        ).size().isEqualTo(2);
    }
}
