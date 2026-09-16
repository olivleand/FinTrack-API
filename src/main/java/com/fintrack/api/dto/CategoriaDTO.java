package com.fintrack.api.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criar ou alterar uma categoria")
public record CategoriaDTO(

        @NotBlank
        String nome
) {
}
