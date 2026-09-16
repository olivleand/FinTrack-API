package com.fintrack.api.repository;

import com.fintrack.api.model.TipoTransacao;
import com.fintrack.api.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByUsuarioId(Long usuarioId);

    List<Transacao> findByUsuarioIdAndDataBetween(
            Long usuarioId,
            LocalDate inicio,
            LocalDate fim
    );

    @Query("""
            SELECT COALESCE(SUM(t.valor), 0)
            FROM Transacao t
            WHERE t.usuario.id = :usuarioId
            AND t.tipo = :tipo
            """)
    BigDecimal sumValorByUsuarioIdAndTipo(
            @Param("usuarioId") Long usuarioId,
            @Param("tipo") TipoTransacao tipo
    );

    List<Transacao> findByUsuarioIdAndCategoriaId(
            Long usuarioId,
            Long categoriaId
    );

    java.util.Optional<Transacao> findByIdAndUsuarioId(Long id, Long usuarioId);
}
