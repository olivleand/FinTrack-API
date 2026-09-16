package com.fintrack.api.controller;

import com.fintrack.api.dto.CategoriaDTO;
import com.fintrack.api.dto.CategoriaResponseDTO;
import com.fintrack.api.model.Usuario;
import com.fintrack.api.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@Tag(name = "Categorias")
@SecurityRequirement(name = "bearerAuth")
public class CategoriaController {
    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    @Operation(summary = "Cria uma categoria para o usuário autenticado")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Categoria criada"), @ApiResponse(responseCode = "400", description = "Dados inválidos"), @ApiResponse(responseCode = "401", description = "Não autenticado")})
    public ResponseEntity<CategoriaResponseDTO> criar(@Valid @RequestBody CategoriaDTO dto, @AuthenticationPrincipal Usuario usuario) {
        CategoriaResponseDTO resposta = categoriaService.criar(dto, usuario);
        return ResponseEntity.created(URI.create("/api/v1/categorias/" + resposta.id())).body(resposta);
    }

    @GetMapping
    @Operation(summary = "Lista as categorias do usuário autenticado")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Categorias encontradas"), @ApiResponse(responseCode = "401", description = "Não autenticado")})
    public ResponseEntity<List<CategoriaResponseDTO>> listar(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(categoriaService.listar(usuario));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@categoriaService.isOwner(authentication, #id)")
    @Operation(summary = "Busca uma categoria própria pelo ID")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Categoria encontrada"), @ApiResponse(responseCode = "401", description = "Não autenticado"), @ApiResponse(responseCode = "403", description = "Não é o proprietário"), @ApiResponse(responseCode = "404", description = "Categoria não encontrada")})
    public ResponseEntity<CategoriaResponseDTO> buscar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(categoriaService.buscar(id, usuario));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@categoriaService.isOwner(authentication, #id)")
    @Operation(summary = "Atualiza uma categoria própria")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Categoria atualizada"), @ApiResponse(responseCode = "400", description = "Dados inválidos"), @ApiResponse(responseCode = "401", description = "Não autenticado"), @ApiResponse(responseCode = "403", description = "Não é o proprietário"), @ApiResponse(responseCode = "404", description = "Categoria não encontrada")})
    public ResponseEntity<CategoriaResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaDTO dto, @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(categoriaService.atualizar(id, dto, usuario));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@categoriaService.isOwner(authentication, #id)")
    @Operation(summary = "Exclui uma categoria própria")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Categoria excluída"), @ApiResponse(responseCode = "401", description = "Não autenticado"), @ApiResponse(responseCode = "403", description = "Não é o proprietário"), @ApiResponse(responseCode = "404", description = "Categoria não encontrada"), @ApiResponse(responseCode = "409", description = "Categoria em uso por transações")})
    public ResponseEntity<Void> excluir(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        categoriaService.excluir(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
