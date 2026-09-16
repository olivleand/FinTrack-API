package com.fintrack.api.dto;

import com.fintrack.api.model.TipoTransacao;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Transação financeira do usuário autenticado")
public record TransacaoResponseDTO(
        Long id,
        String descricao,
        BigDecimal valor,
        TipoTransacao tipo,
        LocalDate data,
        CategoriaResponseDTO categoria
) {
}
