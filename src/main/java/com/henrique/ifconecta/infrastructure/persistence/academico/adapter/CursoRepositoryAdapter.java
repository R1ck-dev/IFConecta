package com.henrique.ifconecta.infrastructure.persistence.academico.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.academico.model.Curso;
import com.henrique.ifconecta.domain.academico.port.CursoRepository;
import com.henrique.ifconecta.infrastructure.persistence.academico.mapper.CursoMapper;
import com.henrique.ifconecta.infrastructure.persistence.academico.repository.SpringDataCursoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CursoRepositoryAdapter implements CursoRepository{
    
    private final SpringDataCursoRepository repository;
    private final CursoMapper mapper;

    @Override
    public Curso salvar(Curso curso) {
        return mapper.toDomain(repository.save(mapper.toEntity(curso)));
    }

    @Override
    public Optional<Curso> buscarPorId(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existePorId(UUID id) {
        return repository.existsById(id);
    }
}
