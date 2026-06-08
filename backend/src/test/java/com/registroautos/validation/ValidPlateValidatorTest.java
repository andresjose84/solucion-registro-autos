package com.registroautos.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ValidPlateValidatorTest {

    private ValidPlateValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ValidPlateValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {"MWK737", "mwk737", " ABC123 ", "XYZ456"})
    void shouldAcceptValidPlates(String plate) {
        assertThat(validator.isValid(plate, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABC-123", "AB1234", "ABCD123", "123ABC", "MWK73"})
    void shouldRejectInvalidPlates(String plate) {
        assertThat(validator.isValid(plate, null)).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldAllowBlankValuesForBeanValidationDelegation(String plate) {
        assertThat(validator.isValid(plate, null)).isTrue();
    }
}
