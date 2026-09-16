package com.fintrack.api.service;

import com.fintrack.api.dto.LoginDTO;
import com.fintrack.api.dto.RegistroDTO;
import com.fintrack.api.dto.UsuarioResponseDTO;
import com.fintrack.api.exception.ConflictException;
import com.fintrack.api.model.Usuario;
import com.fintrack.api.repository.UsuarioRepository;
import com.fintrack.api.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UsuarioResponseDTO registrar(RegistroDTO dto) {

        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new ConflictException(
                    "E-mail já cadastrado."
            );
        }

        String senhaCriptografada =
                passwordEncoder.encode(dto.senha());

        Usuario usuario = new Usuario(
                dto.nome(),
                dto.email(),
                senhaCriptografada
        );

        Usuario salvo = usuarioRepository.save(usuario);
        return new UsuarioResponseDTO(salvo.getId(), salvo.getNome(), salvo.getEmail());
    }

    public String login(LoginDTO dto) {

        Usuario usuario = usuarioRepository
                .findByEmail(dto.email())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "E-mail ou senha inválidos."
                        )
                );

        if (!passwordEncoder.matches(
                dto.senha(),
                usuario.getSenha()
        )) {
            throw new IllegalArgumentException(
                    "E-mail ou senha inválidos."
            );
        }

        return jwtService.gerarToken(
                usuario.getEmail()
        );
    }
}
