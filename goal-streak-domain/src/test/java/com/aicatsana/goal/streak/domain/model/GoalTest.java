package com.aicatsana.goal.streak.domain.model;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GoalTest {

    @Test
    void whenValidData_successful() {
        // prepare
        String validGoalName = "Readbook";
        int validGoalDurationInDays = 30;

        // test
        Goal goal = new Goal(validGoalName, validGoalDurationInDays);

        // assert
        assertThat(goal).satisfies(g -> {
            assertThat(g.goalName()).isEqualTo(validGoalName);
            assertThat(g.goalDurationInDays()).isEqualTo(validGoalDurationInDays);
        });
    }

    @Test
    void whenGoalDurationInDaysIsZero_successful() {
        // prepare
        String validGoalName = "Read a book";
        int validGoalDurationInDays = 0;

        // test
        Goal goal = new Goal(validGoalName, validGoalDurationInDays);

        // assert
        assertThat(goal).satisfies(g -> {
            assertThat(g.goalName()).isEqualTo(validGoalName);
            assertThat(g.goalDurationInDays()).isEqualTo(validGoalDurationInDays);
        });
    }

    @Test
    void whenGoalDurationInDaysIsNegative_throwsValidationException() {
        // prepare
        String validGoalName = "Read a book";
        int invalidGoalDurationInDays = -30;

        // test
        assertThatThrownBy(() -> new Goal(validGoalName, invalidGoalDurationInDays))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Goal.goalDurationInDays must be greater than or equal to 0");
    }

    @ParameterizedTest
    @MethodSource("invalidGoalNames")
    void whenInvalidGoalName_throwsValidationException(String invalidGoalName) {
        // prepare
        int validGoalDurationInDays = 30;

        // test
        assertThatThrownBy(() -> new Goal(invalidGoalName, validGoalDurationInDays))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Goal.goalName Goal name must be 3-20 printable characters");
    }

    static Stream<String> invalidGoalNames() {
        return Stream.of(
                " ",
                null,
                "o".repeat(21),
                "o");
    }
}
