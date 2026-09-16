package com.fintrack.api.dto;

import com.fintrack.api.model.TipoTransacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Dados para criar ou alterar uma transação")
public record TransacaoDTO(

        @NotBlank
        String descricao,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal valor,

        @NotNull
        TipoTransacao tipo,

        @NotNull
        LocalDate data,

        @NotNull
        Long categoriaId
) {
}
