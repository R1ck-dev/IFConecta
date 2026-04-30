package com.henrique.ifconecta.infrastructure.persistence.academico.mapper;

import java.util.HashSet;

import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.academico.model.Turma;
import com.henrique.ifconecta.infrastructure.persistence.academico.entity.DisciplinaJpaEntity;
import com.henrique.ifconecta.infrastructure.persistence.academico.entity.TurmaJpaEntity;
import com.henrique.ifconecta.infrastructure.persistence.usuario.entity.UsuarioJpaEntity;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TurmaMapper {

    private final EntityManager entityManager;

    public TurmaJpaEntity toEntity(Turma domain) {
        TurmaJpaEntity entity = new TurmaJpaEntity();
        entity.setId(domain.getId());
        entity.setSemestre(domain.getSemestre());
        entity.setCodigoTurma(domain.getCodigoTurma());
        entity.setDataCriacao(domain.getDataCriacao());
        entity.setAlunosMatriculados(new HashSet<>(domain.getAlunosMatriculados()));
        
        entity.setDisciplina(entityManager.getReference(DisciplinaJpaEntity.class, domain.getDisciplinaId()));
        entity.setProfessor(entityManager.getReference(UsuarioJpaEntity.class, domain.getProfessorId()));
        
        return entity;
    }

    public Turma toDomain(TurmaJpaEntity entity) {
        return new Turma(
                entity.getId(),
                entity.getDisciplina().getId(),
                entity.getProfessor().getId(),
                entity.getSemestre(),
                entity.getCodigoTurma(),
                entity.getDataCriacao(),
                new HashSet<>(entity.getAlunosMatriculados())
        );
    }
}