package com.henrique.ifconecta.infrastructure.persistence.academico.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.henrique.ifconecta.domain.academico.enums.StatusTurma;
import com.henrique.ifconecta.infrastructure.persistence.academico.entity.TurmaJpaEntity;

public interface SpringDataTurmaRepository extends JpaRepository<TurmaJpaEntity, UUID> {

    @Query("SELECT a FROM TurmaJpaEntity t JOIN t.alunosMatriculados a WHERE t.id = :turmaId")
    List<UUID> findIdsAlunosMatriculadosByTurmaId(@Param("turmaId") UUID turmaId);

    @Query("""
            SELECT t FROM TurmaJpaEntity t
            WHERE t.status = com.henrique.ifconecta.domain.academico.enums.StatusTurma.ATIVA
              AND (:disciplinaId IS NULL OR t.disciplina.id = :disciplinaId)
              AND (:semestre IS NULL OR t.semestre = :semestre)
            ORDER BY t.dataCriacao DESC
            """)
    Page<TurmaJpaEntity> findAtivasByFiltros(
            @Param("disciplinaId") UUID disciplinaId,
            @Param("semestre") String semestre,
            Pageable pageable);

    @Query("SELECT t FROM TurmaJpaEntity t JOIN t.alunosMatriculados a WHERE a = :alunoId ORDER BY t.semestre DESC, t.codigoTurma")
    List<TurmaJpaEntity> findTurmasMatriculadasByAluno(@Param("alunoId") UUID alunoId);

    List<TurmaJpaEntity> findByProfessorIdAndStatusOrderBySemestreDescCodigoTurmaAsc(UUID professorId, StatusTurma status);
}
