package com.henrique.ifconecta.infrastructure.persistence.academico.adapter;

import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.academico.model.Disciplina;
import com.henrique.ifconecta.domain.academico.port.DisciplinaRepository;
import com.henrique.ifconecta.infrastructure.persistence.academico.mapper.DisciplinaMapper;
import com.henrique.ifconecta.infrastructure.persistence.academico.repository.SpringDataDisciplinaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DisciplinaRepositoryAdapter implements DisciplinaRepository {

    private final SpringDataDisciplinaRepository repository;
    private final DisciplinaMapper mapper;

    @Override
    public Disciplina salvar(Disciplina disciplina) {
        return mapper.toDomain(repository.save(mapper.toEntity(disciplina)));
    }

}
