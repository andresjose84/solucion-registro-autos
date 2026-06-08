package com.registroautos.exception;

import com.registroautos.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/cars");
    }

    @Test
    void handleNotFound_shouldReturn404() {
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(
                new ResourceNotFoundException("Auto no encontrado"),
                request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Auto no encontrado");
        assertThat(response.getBody().path()).isEqualTo("/api/v1/cars");
    }

    @Test
    void handleDuplicate_shouldReturn409() {
        ResponseEntity<ErrorResponse> response = handler.handleDuplicate(
                new DuplicateResourceException("El email ya esta registrado"),
                request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().message()).isEqualTo("El email ya esta registrado");
    }

    @Test
    void handleBadCredentials_shouldReturn401() {
        ResponseEntity<ErrorResponse> response = handler.handleBadCredentials(
                new BadCredentialsException("bad"),
                request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().message()).isEqualTo("Credenciales invalidas");
    }

    @Test
    void handleValidation_shouldReturn400WithFieldErrors() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "carRequest");
        bindingResult.addError(new FieldError("carRequest", "plate", "Placa invalida"));

        MethodParameter parameter = new MethodParameter(
                ValidationTarget.class.getDeclaredMethod("validate", String.class),
                0
        );
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().fieldErrors()).containsEntry("plate", "Placa invalida");
    }

    @SuppressWarnings("unused")
    private static class ValidationTarget {
        void validate(String plate) {
        }
    }
}
