package com.registroautos.controller;

import com.registroautos.dto.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;

@RestController
@Tag(name = "Health")
public class HealthController {

    private final DataSource dataSource;
    private final String serviceName;

    public HealthController(
            DataSource dataSource,
            @Value("${spring.application.name}") String serviceName
    ) {
        this.dataSource = dataSource;
        this.serviceName = serviceName;
    }

    @GetMapping("/health")
    @Operation(summary = "Consultar el estado del backend")
    public ResponseEntity<HealthResponse> health() {
        boolean databaseUp = isDatabaseUp();
        String status = databaseUp ? "UP" : "DOWN";
        String databaseStatus = databaseUp ? "UP" : "DOWN";

        HealthResponse response = new HealthResponse(
                status,
                serviceName,
                LocalDateTime.now(),
                new HealthResponse.DatabaseHealth(databaseStatus)
        );

        HttpStatus httpStatus = databaseUp ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(httpStatus).body(response);
    }

    private boolean isDatabaseUp() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2);
        } catch (Exception ex) {
            return false;
        }
    }
}
