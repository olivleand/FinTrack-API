package com.fintrack.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Categoria pertencente ao usuário autenticado")
public record CategoriaResponseDTO(Long id, String nome) {
}
