package com.fintrack.api.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumo financeiro do usuário autenticado")
public record SaldoDTO(
        BigDecimal receitas,
        BigDecimal despesas,
        BigDecimal saldo
) {
}
