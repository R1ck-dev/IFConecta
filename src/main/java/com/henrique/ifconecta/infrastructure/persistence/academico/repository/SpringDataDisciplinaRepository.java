package com.henrique.ifconecta.infrastructure.persistence.academico.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.henrique.ifconecta.infrastructure.persistence.academico.entity.DisciplinaJpaEntity;

public interface SpringDataDisciplinaRepository extends JpaRepository<DisciplinaJpaEntity, UUID> {

}
