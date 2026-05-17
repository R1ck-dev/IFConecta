package com.henrique.ifconecta.infrastructure.persistence.academico.adapter;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Override
    public List<Disciplina> listarTodas(UUID cursoIdFiltro) {
        List<?> entities = cursoIdFiltro != null
                ? repository.findByCursoIdOrderByNomeAsc(cursoIdFiltro)
                : repository.findAllByOrderByNomeAsc();
        return entities.stream()
                .map(e -> mapper.toDomain((com.henrique.ifconecta.infrastructure.persistence.academico.entity.DisciplinaJpaEntity) e))
                .collect(Collectors.toList());
    }

    @Override
    public List<Disciplina> buscarPorIds(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return repository.findAllById(ids).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
