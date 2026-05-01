package com.henrique.ifconecta.infrastructure.persistence.notificacao.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.henrique.ifconecta.infrastructure.persistence.notificacao.entity.NotificacaoJpaEntity;

public interface SpringDataNotificacaoRepository extends JpaRepository<NotificacaoJpaEntity, UUID> {
}
