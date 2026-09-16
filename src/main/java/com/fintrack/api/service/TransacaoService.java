package com.fintrack.api.service;

import com.fintrack.api.dto.CategoriaResponseDTO;
import com.fintrack.api.dto.SaldoDTO;
import com.fintrack.api.dto.TransacaoDTO;
import com.fintrack.api.dto.TransacaoResponseDTO;
import com.fintrack.api.exception.ResourceNotFoundException;
import com.fintrack.api.exception.SaldoInsuficienteException;
import com.fintrack.api.model.Categoria;
import com.fintrack.api.model.TipoTransacao;
import com.fintrack.api.model.Transacao;
import com.fintrack.api.model.Usuario;
import com.fintrack.api.repository.CategoriaRepository;
import com.fintrack.api.repository.TransacaoRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransacaoService {
    private final TransacaoRepository transacaoRepository;
    private final CategoriaRepository categoriaRepository;

    public TransacaoService(TransacaoRepository transacaoRepository, CategoriaRepository categoriaRepository) {
        this.transacaoRepository = transacaoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public TransacaoResponseDTO criar(TransacaoDTO dto, Usuario usuario) {
        validarSaldo(dto, usuario, null);
        Categoria categoria = categoriaDoUsuario(dto.categoriaId(), usuario.getId());
        return toResponse(transacaoRepository.save(toEntity(dto, usuario, categoria)));
    }

    public List<TransacaoResponseDTO> listar(Usuario usuario, LocalDate inicio, LocalDate fim, Long categoriaId) {
        List<Transacao> transacoes;
        if (categoriaId != null) {
            categoriaDoUsuario(categoriaId, usuario.getId());
            transacoes = transacaoRepository.findByUsuarioIdAndCategoriaId(usuario.getId(), categoriaId);
        } else if (inicio != null && fim != null) {
            transacoes = transacaoRepository.findByUsuarioIdAndDataBetween(usuario.getId(), inicio, fim);
        } else {
            transacoes = transacaoRepository.findByUsuarioId(usuario.getId());
        }
        return transacoes.stream().map(this::toResponse).toList();
    }

    public TransacaoResponseDTO buscar(Long id, Usuario usuario) {
        return toResponse(obterDoUsuario(id, usuario.getId()));
    }

    public TransacaoResponseDTO atualizar(Long id, TransacaoDTO dto, Usuario usuario) {
        Transacao transacao = obterDoUsuario(id, usuario.getId());
        validarSaldo(dto, usuario, transacao);
        Categoria categoria = categoriaDoUsuario(dto.categoriaId(), usuario.getId());
        transacao.setDescricao(dto.descricao());
        transacao.setValor(dto.valor());
        transacao.setTipo(dto.tipo());
        transacao.setData(dto.data());
        transacao.setCategoria(categoria);
        return toResponse(transacaoRepository.save(transacao));
    }

    public void excluir(Long id, Usuario usuario) {
        transacaoRepository.delete(obterDoUsuario(id, usuario.getId()));
    }

    public SaldoDTO saldo(Usuario usuario) {
        BigDecimal receitas = total(usuario.getId(), TipoTransacao.RECEITA);
        BigDecimal despesas = total(usuario.getId(), TipoTransacao.DESPESA);
        return new SaldoDTO(receitas, despesas, receitas.subtract(despesas));
    }

    public boolean isOwner(Authentication authentication, Long id) {
        return authentication != null && authentication.getPrincipal() instanceof Usuario usuario
                && transacaoRepository.findByIdAndUsuarioId(id, usuario.getId()).isPresent();
    }

    private void validarSaldo(TransacaoDTO dto, Usuario usuario, Transacao anterior) {
        if (dto.tipo() != TipoTransacao.DESPESA) return;
        BigDecimal saldoAtual = saldo(usuario).saldo();
        if (anterior != null && anterior.getTipo() == TipoTransacao.DESPESA) saldoAtual = saldoAtual.add(anterior.getValor());
        if (dto.valor().compareTo(saldoAtual) > 0) throw new SaldoInsuficienteException("Saldo insuficiente.");
    }

    private BigDecimal total(Long usuarioId, TipoTransacao tipo) {
        BigDecimal total = transacaoRepository.sumValorByUsuarioIdAndTipo(usuarioId, tipo);
        return total == null ? BigDecimal.ZERO : total;
    }

    private Categoria categoriaDoUsuario(Long id, Long usuarioId) {
        return categoriaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));
    }

    private Transacao obterDoUsuario(Long id, Long usuarioId) {
        return transacaoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada."));
    }

    private Transacao toEntity(TransacaoDTO dto, Usuario usuario, Categoria categoria) {
        return new Transacao(dto.descricao(), dto.valor(), dto.tipo(), dto.data(), usuario, categoria);
    }

    private TransacaoResponseDTO toResponse(Transacao transacao) {
        Categoria categoria = transacao.getCategoria();
        return new TransacaoResponseDTO(transacao.getId(), transacao.getDescricao(), transacao.getValor(),
                transacao.getTipo(), transacao.getData(), new CategoriaResponseDTO(categoria.getId(), categoria.getNome()));
    }
}
