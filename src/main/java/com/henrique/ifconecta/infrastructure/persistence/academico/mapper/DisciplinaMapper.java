package com.henrique.ifconecta.infrastructure.persistence.academico.mapper;

import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.academico.model.Disciplina;
import com.henrique.ifconecta.infrastructure.persistence.academico.entity.CursoJpaEntity;
import com.henrique.ifconecta.infrastructure.persistence.academico.entity.DisciplinaJpaEntity;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DisciplinaMapper {

    private final EntityManager entityManager;

    public DisciplinaJpaEntity toEntity(Disciplina domain) {
        DisciplinaJpaEntity entity = new DisciplinaJpaEntity();
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setCargaHoraria(domain.getCargaHoraria());
        entity.setCurso(entityManager.getReference(CursoJpaEntity.class, domain.getCursoId()));
        return entity;
    }

    public Disciplina toDomain(DisciplinaJpaEntity entity) {
        return new Disciplina(
                entity.getId(),
                entity.getCurso().getId(),
                entity.getNome(),
                entity.getCargaHoraria());
    }
}