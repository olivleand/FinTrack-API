package com.fintrack.api.service;

import com.fintrack.api.dto.CategoriaDTO;
import com.fintrack.api.dto.CategoriaResponseDTO;
import com.fintrack.api.exception.ResourceNotFoundException;
import com.fintrack.api.model.Categoria;
import com.fintrack.api.model.Usuario;
import com.fintrack.api.repository.CategoriaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public CategoriaResponseDTO criar(CategoriaDTO dto, Usuario usuario) {
        return toResponse(categoriaRepository.save(new Categoria(dto.nome(), usuario)));
    }

    public List<CategoriaResponseDTO> listar(Usuario usuario) {
        return categoriaRepository.findByUsuarioId(usuario.getId()).stream().map(this::toResponse).toList();
    }

    public CategoriaResponseDTO buscar(Long id, Usuario usuario) {
        return toResponse(obterDoUsuario(id, usuario.getId()));
    }

    public CategoriaResponseDTO atualizar(Long id, CategoriaDTO dto, Usuario usuario) {
        Categoria categoria = obterDoUsuario(id, usuario.getId());
        categoria.setNome(dto.nome());
        return toResponse(categoriaRepository.save(categoria));
    }

    public void excluir(Long id, Usuario usuario) {
        categoriaRepository.delete(obterDoUsuario(id, usuario.getId()));
    }

    public boolean isOwner(Authentication authentication, Long id) {
        return authentication != null && authentication.getPrincipal() instanceof Usuario usuario
                && categoriaRepository.findByIdAndUsuarioId(id, usuario.getId()).isPresent();
    }

    private Categoria obterDoUsuario(Long id, Long usuarioId) {
        return categoriaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));
    }

    private CategoriaResponseDTO toResponse(Categoria categoria) {
        return new CategoriaResponseDTO(categoria.getId(), categoria.getNome());
    }
}
