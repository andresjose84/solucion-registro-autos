package com.registroautos.dto;

import com.registroautos.validation.ValidPlate;
import com.registroautos.validation.ValidYear;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CarRequest(
        @NotBlank(message = "La marca es obligatoria")
        @Size(max = 100, message = "La marca no puede superar 100 caracteres")
        String brand,

        @NotBlank(message = "El modelo es obligatorio")
        @Size(max = 100, message = "El modelo no puede superar 100 caracteres")
        String model,

        @NotNull(message = "El ano es obligatorio")
        @ValidYear
        Integer year,

        @NotBlank(message = "La placa es obligatoria")
        @ValidPlate
        String plate,

        @NotBlank(message = "El color es obligatorio")
        @Size(max = 50, message = "El color no puede superar 50 caracteres")
        String color,

        @Size(max = 500, message = "La URL de la foto no puede superar 500 caracteres")
        String photoUrl
) {
}
