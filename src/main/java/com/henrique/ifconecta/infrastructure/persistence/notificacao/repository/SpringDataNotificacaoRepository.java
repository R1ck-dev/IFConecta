package com.henrique.ifconecta.infrastructure.persistence.notificacao.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.henrique.ifconecta.infrastructure.persistence.notificacao.entity.NotificacaoJpaEntity;

public interface SpringDataNotificacaoRepository extends JpaRepository<NotificacaoJpaEntity, UUID> {
    Page<NotificacaoJpaEntity> findAllByUsuarioIdOrderByDataCriacaoDesc(UUID usuarioId, Pageable pageable);

    long countByUsuarioIdAndLidaFalse(UUID usuarioId);

    @Modifying
    @Query("UPDATE NotificacaoJpaEntity n SET n.lida = true WHERE n.usuario.id = :usuarioId AND n.lida = false")
    int marcarTodasComoLidasPorUsuario(@Param("usuarioId") UUID usuarioId);
}
