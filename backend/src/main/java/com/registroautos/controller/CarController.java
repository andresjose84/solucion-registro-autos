package com.registroautos.controller;

import com.registroautos.dto.CarRequest;
import com.registroautos.dto.CarResponse;
import com.registroautos.security.CustomUserDetailsService;
import com.registroautos.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cars")
@Tag(name = "Autos")
@SecurityRequirement(name = "bearerAuth")
public class CarController {

    private final CarService carService;
    private final CustomUserDetailsService userDetailsService;

    public CarController(CarService carService, CustomUserDetailsService userDetailsService) {
        this.carService = carService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping
    @Operation(summary = "Listar autos del usuario autenticado")
    public ResponseEntity<List<CarResponse>> listCars(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Integer year
    ) {
        Long userId = resolveUserId(userDetails);
        return ResponseEntity.ok(carService.findAllForUser(userId, search, brand, year));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de un auto")
    public ResponseEntity<CarResponse> getCar(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        Long userId = resolveUserId(userDetails);
        return ResponseEntity.ok(carService.findByIdForUser(userId, id));
    }

    @PostMapping
    @Operation(summary = "Crear un auto")
    public ResponseEntity<CarResponse> createCar(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CarRequest request
    ) {
        Long userId = resolveUserId(userDetails);
        CarResponse response = carService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un auto")
    public ResponseEntity<CarResponse> updateCar(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody CarRequest request
    ) {
        Long userId = resolveUserId(userDetails);
        return ResponseEntity.ok(carService.update(userId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un auto")
    public ResponseEntity<Void> deleteCar(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        Long userId = resolveUserId(userDetails);
        carService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }

    private Long resolveUserId(UserDetails userDetails) {
        return userDetailsService.loadEntityByEmail(userDetails.getUsername()).getId();
    }
}
