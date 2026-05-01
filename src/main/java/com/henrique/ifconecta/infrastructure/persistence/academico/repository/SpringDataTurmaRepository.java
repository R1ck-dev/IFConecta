package com.henrique.ifconecta.infrastructure.persistence.academico.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.henrique.ifconecta.infrastructure.persistence.academico.entity.TurmaJpaEntity;

public interface SpringDataTurmaRepository extends JpaRepository<TurmaJpaEntity, UUID> {
    @Query("SELECT a FROM TurmaJpaEntity t JOIN t.alunosMatriculados a WHERE t.id = :turmaId")
    List<UUID> findIdsAlunosMatriculadosByTurmaId(@Param("turmaId") UUID turmaId);
}
