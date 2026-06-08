package com.registroautos.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;

class ValidYearValidatorTest {

    private ValidYearValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ValidYearValidator();
    }

    @Test
    void shouldAllowNullForBeanValidationDelegation() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {1900, 2000, 2020})
    void shouldAcceptValidYears(int year) {
        if (year <= Year.now().getValue()) {
            assertThat(validator.isValid(year, null)).isTrue();
        }
    }

    @Test
    void shouldAcceptCurrentYear() {
        assertThat(validator.isValid(Year.now().getValue(), null)).isTrue();
    }

    @Test
    void shouldRejectFutureYear() {
        assertThat(validator.isValid(Year.now().getValue() + 1, null)).isFalse();
    }

    @Test
    void shouldRejectYearBefore1900() {
        assertThat(validator.isValid(1899, null)).isFalse();
    }
}
