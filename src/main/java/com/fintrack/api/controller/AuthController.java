package com.fintrack.api.controller;

import com.fintrack.api.dto.LoginDTO;
import com.fintrack.api.dto.RegistroDTO;
import com.fintrack.api.dto.TokenDTO;
import com.fintrack.api.dto.UsuarioResponseDTO;
import com.fintrack.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Registro e login de usuários")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registra um novo usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    })
    public ResponseEntity<UsuarioResponseDTO> registrar(
            @Valid @RequestBody RegistroDTO dto
    ) {
        UsuarioResponseDTO usuario = usuarioService.registrar(dto);

        return ResponseEntity
                .created(URI.create("/api/v1/usuarios/" + usuario.id()))
                .body(usuario);
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica um usuário e retorna um JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token gerado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "E-mail ou senha inválidos")
    })
    public ResponseEntity<TokenDTO> login(
            @Valid @RequestBody LoginDTO dto
    ) {
        String token = usuarioService.login(dto);

        return ResponseEntity.ok(
                new TokenDTO(token)
        );
    }
}

