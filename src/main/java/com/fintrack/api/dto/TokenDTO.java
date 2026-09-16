package com.fintrack.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token JWT retornado após o login")
public record TokenDTO(
        String token
) {
}
