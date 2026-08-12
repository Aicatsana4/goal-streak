package com.aicatsana.goal.streak.infra.file;

import com.aicatsana.goal.streak.domain.model.Goal;
import com.aicatsana.goal.streak.infra.exception.GoalAlreadyExistsException;
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
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GoalFileReaderTest {

    @TempDir
    private Path tempDir;
    private Path file;
    private GoalFileReader goalFileReader;
    private ObjectMapper mapper;

    @BeforeEach
    void createFile() throws IOException {
        this.file = tempDir.resolve("goals.json");
        this.mapper = new ObjectMapper();
        this.goalFileReader = new GoalFileReader(new GoalFileReaderProperties(file.toString()));
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
    void write_whenValidDataAndEmptyFile_successful() throws IOException {
        // prepare
        Goal actualGoal = new Goal("my goal", 20);

        // test
        goalFileReader.write(actualGoal);

        // assert
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {
        });

        assertThat(content).satisfiesExactly(
                goal -> {
                    assertThat(goal.goalName()).isEqualTo(actualGoal.goalName());
                    assertThat(goal.goalDurationInDays()).isEqualTo(actualGoal.goalDurationInDays());
                }
        );
    }

    @Test
    void write_whenValidData_successful() throws IOException {
        // prepare
        Goal actualGoal1 = new Goal("my goal 1", 10);
        Goal actualGoal2 = new Goal("my goal 2", 20);
        mapper.writeValue(file.toFile(), Set.of(actualGoal1));

        // test
        goalFileReader.write(actualGoal2);

        // assert
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {
        });

        assertThat(content).satisfiesExactlyInAnyOrder(
                goal -> {
                    assertThat(goal.goalName()).isEqualTo(actualGoal1.goalName());
                    assertThat(goal.goalDurationInDays()).isEqualTo(actualGoal1.goalDurationInDays());
                },
                goal -> {
                    assertThat(goal.goalName()).isEqualTo(actualGoal2.goalName());
                    assertThat(goal.goalDurationInDays()).isEqualTo(actualGoal2.goalDurationInDays());
                }
        );
    }

    @Test
    void write_whenAlreadyExistingGoalName_noChange() throws IOException {
        // prepare
        Goal actualGoal1 = new Goal("my goal", 10);
        Goal actualGoalwithExistingName = new Goal("my goal", 20);
        mapper.writeValue(file.toFile(), Set.of(actualGoal1));

        // test & assert
        assertThatThrownBy(() -> goalFileReader.write(actualGoalwithExistingName))
                .isInstanceOf(GoalAlreadyExistsException.class)
                .hasMessageContaining("Goal already exists");
    }

    @Test
    void write_whenNullGoal_throwsIllegalArgumentException() {
        // prepare
        Goal nullGoal = null;

        // test & assert
        assertThatThrownBy(() -> goalFileReader.write(nullGoal))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid goal.");
    }

    @Test
    void readAll_whenValidData_successful() throws IOException {
        // prepare
        Goal actualGoal1 = new Goal("my goal 1", 20);
        Goal actualGoal2 = new Goal("my goal 2", 30);
        mapper.writeValue(file.toFile(), Set.of(actualGoal1, actualGoal2));

        // test
        Set<Goal> readContent = goalFileReader.readAll().orElse(Collections.emptySet());

        // assert
        assertThat(readContent).satisfiesExactlyInAnyOrder(
                goal -> {
                    assertThat(goal.goalName()).isEqualTo(actualGoal1.goalName());
                    assertThat(goal.goalDurationInDays()).isEqualTo(actualGoal1.goalDurationInDays());
                },
                goal -> {
                    assertThat(goal.goalName()).isEqualTo(actualGoal2.goalName());
                    assertThat(goal.goalDurationInDays()).isEqualTo(actualGoal2.goalDurationInDays());
                }
        );
    }

    @Test
    void readAll_whenFileDoesNotExist_returnsEmpty() throws IOException {
        // prepare
        Files.deleteIfExists(file);

        // test
        Set<Goal> readContent = goalFileReader.readAll().orElse(Collections.emptySet());

        // assert
        assertThat(readContent).isEmpty();
    }

    @Test
    void readByGoalName_whenValidData_successful() throws IOException {
        // prepare
        Goal actualGoal1 = new Goal("my goal 1", 20);
        Goal actualGoal2 = new Goal("my goal 2", 30);
        mapper.writeValue(file.toFile(), Set.of(actualGoal1, actualGoal2));

        // test
        Set<Goal> readContent = goalFileReader.readByGoalName(actualGoal1.goalName()).orElse(Collections.emptySet());

        // assert
        assertThat(readContent).satisfiesExactly(
                goal -> {
                    assertThat(goal.goalName()).isEqualTo(actualGoal1.goalName());
                    assertThat(goal.goalDurationInDays()).isEqualTo(actualGoal1.goalDurationInDays());
                }
        );
    }

    @Test
    void readByGoalName_whenGoalNameDoesNotExist_returnsEmpty() throws IOException {
        // prepare
        String goalName = "non-existent goal";

        // test
        Set<Goal> readContent = goalFileReader.readByGoalName(goalName).orElse(Collections.emptySet());

        // assert
        assertThat(readContent).isEmpty();
    }

    @Test
    void readByGoalName_whenFileDoesNotExist_returnsEmpty() throws IOException {
        // prepare
        Files.deleteIfExists(file);

        // test
        Set<Goal> readContent = goalFileReader.readByGoalName("non-existent goal").orElse(Collections.emptySet());

        // assert
        assertThat(readContent).isEmpty();
    }

    @Test
    void readByGoalName_whenNullGoalName_throwsIllegalArgumentException() {
        // prepare
        String goalName = null;

        // test & assert
        assertThatThrownBy(() -> goalFileReader.readByGoalName(goalName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid goal name.");
    }

    @Test
    void readByGoalName_whenBlankGoalName_throwsIllegalArgumentException() {
        // prepare
        String goalName = "  ";

        // test & assert
        assertThatThrownBy(() -> goalFileReader.readByGoalName(goalName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid goal name.");
    }

    @Test
    void updateByGoalName_whenValidData_successful() throws IOException {
        // prepare
        Goal actualGoal1 = new Goal("my goal 1", 20);
        Goal actualGoal2 = new Goal("my goal 2", 30);
        Goal updatedGoal = new Goal("my updated goal", 40);
        mapper.writeValue(file.toFile(), Set.of(actualGoal1, actualGoal2));

        // test
        goalFileReader.updateByGoalName(actualGoal1.goalName(), updatedGoal);

        // assert
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {
        });
        assertThat(content).satisfiesExactlyInAnyOrder(
                goal -> {
                    assertThat(goal.goalName()).isEqualTo(updatedGoal.goalName());
                    assertThat(goal.goalDurationInDays()).isEqualTo(updatedGoal.goalDurationInDays());
                },
                goal -> {
                    assertThat(goal.goalName()).isEqualTo(actualGoal2.goalName());
                    assertThat(goal.goalDurationInDays()).isEqualTo(actualGoal2.goalDurationInDays());
                }
        );
    }

    @Test
    void updateByGoalName_whenFileIsEmpty_successful() throws IOException {
        // prepare
        String goalName = "my goal";
        Goal updatedGoal = new Goal("my updated goal", 40);

        // test
        goalFileReader.updateByGoalName(goalName, updatedGoal);

        // assert
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {
        });
        assertThat(content).isEmpty();
    }

    @Test
    void updateByGoalName_when_successful() throws IOException {
        // prepare
        String goalName = "my goal";
        Goal updatedGoal = new Goal("my goal", 40);

        // test
        goalFileReader.updateByGoalName(goalName, updatedGoal);

        // assert
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {
        });
        assertThat(content).isEmpty();
    }

    @Test
    void updateByGoalName_whenFileDoesNotExist_throwsFileNotFoundException() throws IOException {
        // prepare
        Files.deleteIfExists(file);
        Goal expectedUpdatedGoal = new Goal("my updated goal", 30);
        String goalName = "my goal";

        // test
        assertThatThrownBy(() -> goalFileReader.updateByGoalName(goalName, expectedUpdatedGoal))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("File not found");
    }

    @Test
    void updateByGoalName_whenNullNewGoal_throwsIllegalArgumentException() {
        // prepare
        Goal updatedGoal = null;
        String goalName = "my goal";

        // test & assert
        assertThatThrownBy(() -> goalFileReader.updateByGoalName(goalName, updatedGoal))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid goal.");
    }

    @Test
    void updateByGoalName_whenNullGoalName_throwsIllegalArgumentException() {
        // prepare
        Goal expectedUpdatedGoal = new Goal("my updated goal", 30);
        String goalName = null;

        // test & assert
        assertThatThrownBy(() -> goalFileReader.updateByGoalName(goalName, expectedUpdatedGoal))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid goal name.");
    }

    @Test
    void updateByGoalName_whenBlankGoalName_throwsIllegalArgumentException() {
        // prepare
        Goal expectedUpdatedGoal = new Goal("my updated goal", 30);
        String goalName = " ";

        // test & assert
        assertThatThrownBy(() -> goalFileReader.updateByGoalName(goalName, expectedUpdatedGoal))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid goal name.");
    }


    @Test
    void updateGoalDurationInDays_whenValidData_successful() throws IOException {
        // prepare
        Goal actualGoal1 = new Goal("my goal 1", 20);
        Goal actualGoal2 = new Goal("my goal 2", 30);
        int newGoalDurationInDays = 50;
        mapper.writeValue(file.toFile(), Set.of(actualGoal1, actualGoal2));

        // test
        goalFileReader.updateGoalDurationInDaysByGoalName(actualGoal1.goalName(), newGoalDurationInDays);


        // assert
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {
        });
        assertThat(content).satisfiesExactlyInAnyOrder(
                goal -> {
                    assertThat(goal.goalName()).isEqualTo(actualGoal1.goalName());
                    assertThat(goal.goalDurationInDays()).isEqualTo(newGoalDurationInDays);
                },
                goal -> {
                    assertThat(goal.goalName()).isEqualTo(actualGoal2.goalName());
                    assertThat(goal.goalDurationInDays()).isEqualTo(actualGoal2.goalDurationInDays());
                }
        );
    }

    @Test
    void updateGoalDurationInDays_whenNegativeDuration_throwsIllegalArgumentException() {
        // prepare
        int goalDurationInDays = -1;
        String goalName = "my goal";

        // test & assert
        assertThatThrownBy(() -> goalFileReader.updateGoalDurationInDaysByGoalName(goalName, goalDurationInDays))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid goal duration.");
    }

    @Test
    void updateGoalDurationInDays_whenNullGoalName_throwsIllegalArgumentException() {
        // prepare
        int goalDurationInDays = 30;
        String goalName = null;

        // test & assert
        assertThatThrownBy(() -> goalFileReader.updateGoalDurationInDaysByGoalName(goalName, goalDurationInDays))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid goal name.");
    }

    @Test
    void updateGoalDurationInDays_whenBlankGoalName_throwsIllegalArgumentException() {
        // prepare
        int goalDurationInDays = 30;
        String goalName = " ";

        // test & assert
        assertThatThrownBy(() -> goalFileReader.updateGoalDurationInDaysByGoalName(goalName, goalDurationInDays))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid goal name.");
    }

    @Test
    void updateGoalDurationInDaysByGoalName_whenFileDoesNotExist_throwsFileNotFoundException() throws IOException {
        // prepare
        Files.deleteIfExists(file);
        int goalDurationInDays = 30;
        String goalName = "my goal";

        // test
        assertThatThrownBy(() -> goalFileReader.updateGoalDurationInDaysByGoalName(goalName, goalDurationInDays))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("File not found");
    }

    @Test
    void updateGoalDurationInDaysByGoalName_whenFileIsEmpty_successful() throws IOException {
        // prepare
        String goalName = "my goal";
        int goalDurationInDays = 40;

        // test
        goalFileReader.updateGoalDurationInDaysByGoalName(goalName, goalDurationInDays);

        // assert
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {
        });
        assertThat(content).isEmpty();
    }

    @Test
    void deleteByGoalName_validWithSingleGoal_successful() throws IOException {
        // prepare
        Goal actualGoal = new Goal("my goal", 20);
        mapper.writeValue(file.toFile(), Set.of(actualGoal));

        // test
        goalFileReader.deleteByGoalName(actualGoal.goalName());

        // assert
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {
        });
        assertThat(content).isEmpty();
    }

    @Test
    void deleteByGoalName_valid_successful() throws IOException {
        // prepare
        Goal actualGoal1 = new Goal("my goal 1", 20);
        Goal actualGoal2 = new Goal("my goal 2", 30);
        mapper.writeValue(file.toFile(), Set.of(actualGoal1, actualGoal2));

        // test
        goalFileReader.deleteByGoalName(actualGoal1.goalName());

        // assert
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {
        });
        assertThat(content).satisfiesExactly(
                goal -> {
                    assertThat(goal.goalName()).isEqualTo(actualGoal2.goalName());
                    assertThat(goal.goalDurationInDays()).isEqualTo(actualGoal2.goalDurationInDays());
                }
        ).size().isEqualTo(1);
    }

    @Test
    void deleteByGoalName_validFromEmptyFile_successful() throws IOException {
        // prepare
        Goal actualGoal1 = new Goal("my goal 1", 20);

        // test
        goalFileReader.deleteByGoalName(actualGoal1.goalName());

        // assert
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {
        });
        assertThat(content).isEmpty();
    }

    @Test
    void deleteByGoalName_whenNullGoalName_throwsIllegalArgumentException() {
        // prepare
        String goalName = null;

        // assert
        assertThatThrownBy(() -> goalFileReader.deleteByGoalName(goalName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid goal name.");
    }

    @Test
    void deleteByGoalName_whenBlankGoalName_throwsIllegalArgumentException() {
        // prepare
        String goalName = " ";

        // assert
        assertThatThrownBy(() -> goalFileReader.deleteByGoalName(goalName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid goal name.");
    }

    @Test
    void deleteByGoalName__NoChange() throws IOException {
        // prepare
        Files.deleteIfExists(file);

        // test & assert
        assertThatNoException().isThrownBy(() -> goalFileReader.deleteByGoalName("my goal"));
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
        Set<Goal> content = mapper.readValue(file.toFile(), new TypeReference<>() {
        });
        assertThat(content).satisfiesExactlyInAnyOrder(
                goal -> {
                    assertThat(goal.goalName()).isEqualTo(actualGoal1.goalName());
                    assertThat(goal.goalDurationInDays()).isEqualTo(actualGoal1.goalDurationInDays());
                },
                goal -> {
                    assertThat(goal.goalName()).isEqualTo(actualGoal2.goalName());
                    assertThat(goal.goalDurationInDays()).isEqualTo(actualGoal2.goalDurationInDays());
                }
        ).size().isEqualTo(2);
    }
}
