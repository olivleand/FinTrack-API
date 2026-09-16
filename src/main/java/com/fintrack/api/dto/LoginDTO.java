package com.fintrack.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Credenciais para autenticação")
public record LoginDTO(

        @NotBlank
        @Email
        String email,

        @NotBlank
        String senha
) {
}
