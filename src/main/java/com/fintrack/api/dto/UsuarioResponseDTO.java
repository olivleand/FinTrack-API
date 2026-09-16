package com.fintrack.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados públicos de um usuário, sem a senha")
public record UsuarioResponseDTO(Long id, String nome, String email) {
}
