package com.fintrack.api.controller;

import com.fintrack.api.dto.SaldoDTO;
import com.fintrack.api.dto.TransacaoDTO;
import com.fintrack.api.dto.TransacaoResponseDTO;
import com.fintrack.api.model.Usuario;
import com.fintrack.api.service.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transacoes")
@Tag(name = "Transações")
@SecurityRequirement(name = "bearerAuth")
public class TransacaoController {
    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @PostMapping
    @Operation(summary = "Cria uma transação para o usuário autenticado")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Transação criada"), @ApiResponse(responseCode = "400", description = "Dados inválidos ou saldo insuficiente"), @ApiResponse(responseCode = "401", description = "Não autenticado"), @ApiResponse(responseCode = "404", description = "Categoria não encontrada")})
    public ResponseEntity<TransacaoResponseDTO> criar(@Valid @RequestBody TransacaoDTO dto, @AuthenticationPrincipal Usuario usuario) {
        TransacaoResponseDTO resposta = transacaoService.criar(dto, usuario);
        return ResponseEntity.created(URI.create("/api/v1/transacoes/" + resposta.id())).body(resposta);
    }

    @GetMapping
    @Operation(summary = "Lista transações próprias, com filtros opcionais")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Transações encontradas"), @ApiResponse(responseCode = "401", description = "Não autenticado"), @ApiResponse(responseCode = "404", description = "Categoria não encontrada")})
    public ResponseEntity<List<TransacaoResponseDTO>> listar(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) Long categoriaId) {
        return ResponseEntity.ok(transacaoService.listar(usuario, inicio, fim, categoriaId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@transacaoService.isOwner(authentication, #id)")
    @Operation(summary = "Busca uma transação própria pelo ID")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Transação encontrada"), @ApiResponse(responseCode = "401", description = "Não autenticado"), @ApiResponse(responseCode = "403", description = "Não é o proprietário"), @ApiResponse(responseCode = "404", description = "Transação não encontrada")})
    public ResponseEntity<TransacaoResponseDTO> buscar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(transacaoService.buscar(id, usuario));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@transacaoService.isOwner(authentication, #id)")
    @Operation(summary = "Atualiza uma transação própria")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Transação atualizada"), @ApiResponse(responseCode = "400", description = "Dados inválidos ou saldo insuficiente"), @ApiResponse(responseCode = "401", description = "Não autenticado"), @ApiResponse(responseCode = "403", description = "Não é o proprietário"), @ApiResponse(responseCode = "404", description = "Transação ou categoria não encontrada")})
    public ResponseEntity<TransacaoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody TransacaoDTO dto, @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(transacaoService.atualizar(id, dto, usuario));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@transacaoService.isOwner(authentication, #id)")
    @Operation(summary = "Exclui uma transação própria")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Transação excluída"), @ApiResponse(responseCode = "401", description = "Não autenticado"), @ApiResponse(responseCode = "403", description = "Não é o proprietário"), @ApiResponse(responseCode = "404", description = "Transação não encontrada")})
    public ResponseEntity<Void> excluir(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        transacaoService.excluir(id, usuario);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/saldo")
    @Operation(summary = "Calcula receitas, despesas e saldo do usuário autenticado")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Saldo calculado"), @ApiResponse(responseCode = "401", description = "Não autenticado")})
    public ResponseEntity<SaldoDTO> saldo(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(transacaoService.saldo(usuario));
    }
}
