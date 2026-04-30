package com.henrique.ifconecta.infrastructure.persistence.academico.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.academico.model.Turma;
import com.henrique.ifconecta.domain.academico.port.TurmaRepository;
import com.henrique.ifconecta.infrastructure.persistence.academico.mapper.TurmaMapper;
import com.henrique.ifconecta.infrastructure.persistence.academico.repository.SpringDataTurmaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TurmaRepositoryAdapter implements TurmaRepository {

    private final SpringDataTurmaRepository jpaRepository;
    private final TurmaMapper mapper;

    @Override
    public Turma salvar(Turma turma) {
        var entity = mapper.toEntity(turma);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Turma> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
