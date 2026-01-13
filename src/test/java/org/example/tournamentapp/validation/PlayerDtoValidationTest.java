package org.example.tournamentapp.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.tournamentapp.model.PlayerDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void createValidTest() {
        PlayerDto dto = PlayerDto.builder()
                .name("ValidName")
                .faction("Faction")
                .build();

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto, CreateGroup.class);

        assertThat(violations).isEmpty();
    }

    @Test
    void createBlankTest() {
        PlayerDto dto = PlayerDto.builder()
                .name("")
                .faction("Faction")
                .build();

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto, CreateGroup.class);

        assertThat(violations).hasSize(2);
    }

    @Test
    void createNullTest() {
        PlayerDto dto = PlayerDto.builder()
                .name(null)
                .faction("Faction")
                .build();

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto, CreateGroup.class);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("У игрока должно быть имя");
    }

    @Test
    void createTooShortTest() {
        PlayerDto dto = PlayerDto.builder()
                .name("A")
                .faction("Faction")
                .build();

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto, CreateGroup.class);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Имя должно содержать от 2 до 35 символов");
    }

    @Test
    void createTooLongTest() {
        PlayerDto dto = PlayerDto.builder()
                .name("A".repeat(36))
                .faction("Faction")
                .build();

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto, CreateGroup.class);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Имя должно содержать от 2 до 35 символов");
    }

    @Test
    void createMinTest() {
        PlayerDto dto = PlayerDto.builder()
                .name("AB")
                .faction("Faction")
                .build();

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto, CreateGroup.class);

        assertThat(violations).isEmpty();
    }

    @Test
    void createMaxTest() {
        PlayerDto dto = PlayerDto.builder()
                .name("A".repeat(35))
                .faction("Faction")
                .build();

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto, CreateGroup.class);

        assertThat(violations).isEmpty();
    }

    @Test
    void selectValidTest() {
        PlayerDto dto = PlayerDto.builder()
                .id(1L)
                .build();

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto, SelectGroup.class);

        assertThat(violations).isEmpty();
    }

    @Test
    void selectNullTest() {
        PlayerDto dto = PlayerDto.builder()
                .id(null)
                .build();

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto, SelectGroup.class);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Выберите игрока");
    }

    @Test
    void scoreValidTest() {
        PlayerDto dto = PlayerDto.builder()
                .ap(50)
                .mp(50)
                .build();

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto, ScoreGroup.class);

        assertThat(violations).isEmpty();
    }

    @Test
    void scoreNullApTest() {
        PlayerDto dto = PlayerDto.builder()
                .ap(null)
                .mp(50)
                .build();

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto, ScoreGroup.class);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Введите AP");
    }

    @Test
    void scoreNullMpTest() {
        PlayerDto dto = PlayerDto.builder()
                .ap(50)
                .mp(null)
                .build();

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto, ScoreGroup.class);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Введите MP");
    }

    @Test
    void scoreNegativeApTest() {
        PlayerDto dto = PlayerDto.builder()
                .ap(-1)
                .mp(50)
                .build();

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto, ScoreGroup.class);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("AP не может быть меньше 0");
    }

    @Test
    void scoreNegativeMpTest() {
        PlayerDto dto = PlayerDto.builder()
                .ap(50)
                .mp(-1)
                .build();

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto, ScoreGroup.class);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("MP не может быть меньше 0");
    }

    @Test
    void scoreApTooLargeTest() {
        PlayerDto dto = PlayerDto.builder()
                .ap(1000)
                .mp(50)
                .build();

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto, ScoreGroup.class);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("AP слишком большой");
    }

    @Test
    void scoreMpTooLargeTest() {
        PlayerDto dto = PlayerDto.builder()
                .ap(50)
                .mp(1000)
                .build();

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto, ScoreGroup.class);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("MP слишком большой");
    }

    @Test
    void scoreMinTest() {
        PlayerDto dto = PlayerDto.builder()
                .ap(0)
                .mp(0)
                .build();

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto, ScoreGroup.class);

        assertThat(violations).isEmpty();
    }

    @Test
    void scoreMaxTest() {
        PlayerDto dto = PlayerDto.builder()
                .ap(999)
                .mp(999)
                .build();

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto, ScoreGroup.class);

        assertThat(violations).isEmpty();
    }

    @Test
    void noGroupTest() {
        PlayerDto dto = PlayerDto.builder().build();

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void createIgnoresIdTest() {
        PlayerDto dto = PlayerDto.builder()
                .id(null)
                .name("ValidName")
                .faction("Faction")
                .build();

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto, CreateGroup.class);

        assertThat(violations).isEmpty();
    }

    @Test
    void selectIgnoresNameTest() {
        PlayerDto dto = PlayerDto.builder()
                .id(1L)
                .name("")
                .build();

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto, SelectGroup.class);

        assertThat(violations).isEmpty();
    }

    @Test
    void scoreIgnoresNameIdTest() {
        PlayerDto dto = PlayerDto.builder()
                .id(null)
                .name("")
                .ap(50)
                .mp(50)
                .build();

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto, ScoreGroup.class);

        assertThat(violations).isEmpty();
    }
}


