package com.henrique.ifconecta.infrastructure.persistence.academico.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.henrique.ifconecta.infrastructure.persistence.academico.entity.TurmaJpaEntity;

public interface SpringDataTurmaRepository extends JpaRepository<TurmaJpaEntity, UUID> {
}
