package com.henrique.ifconecta.infrastructure.persistence.academico.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.academico.model.Turma;
import com.henrique.ifconecta.domain.academico.port.TurmaRepository;
import com.henrique.ifconecta.domain.shared.Pagina;
import com.henrique.ifconecta.infrastructure.persistence.academico.entity.TurmaJpaEntity;
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

    @Override
    public List<UUID> buscarIdsAlunosMatriculados(UUID turmaId) {
        return jpaRepository.findIdsAlunosMatriculadosByTurmaId(turmaId);
    }

    @Override
    public Pagina<Turma> listarTodas(int pagina, int tamanho, UUID disciplinaIdFiltro, String semestreFiltro) {
        String semestre = (semestreFiltro != null && semestreFiltro.isBlank()) ? null : semestreFiltro;
        PageRequest pageRequest = PageRequest.of(pagina, tamanho);
        Page<TurmaJpaEntity> pageJpa = jpaRepository.findAllByFiltros(disciplinaIdFiltro, semestre, pageRequest);

        List<Turma> itens = pageJpa.getContent().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());

        return new Pagina<>(itens, pageJpa.getNumber(), pageJpa.getTotalPages(), pageJpa.getTotalElements());
    }

    @Override
    public List<Turma> listarMatriculadasPorAluno(UUID alunoId) {
        return jpaRepository.findTurmasMatriculadasByAluno(alunoId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Turma> listarLecionadasPorProfessor(UUID professorId) {
        return jpaRepository.findByProfessorIdOrderBySemestreDescCodigoTurmaAsc(professorId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
