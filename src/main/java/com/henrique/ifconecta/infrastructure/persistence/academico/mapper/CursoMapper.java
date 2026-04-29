package com.henrique.ifconecta.infrastructure.persistence.academico.mapper;

import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.academico.model.Curso;
import com.henrique.ifconecta.infrastructure.persistence.academico.entity.CursoJpaEntity;

@Component
public class CursoMapper {
    public CursoJpaEntity toEntity(Curso domain) {
        CursoJpaEntity entity = new CursoJpaEntity();
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setSigla(domain.getSigla());
        entity.setModalidade(domain.getModalidade());
        return entity;
    }

    public Curso toDomain(CursoJpaEntity entity) {
        return new Curso(
                entity.getId(),
                entity.getNome(),
                entity.getSigla(),
                entity.getModalidade());
    }
}
