package com.henrique.ifconecta.application.academico.usecase;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.academico.dto.DisciplinaResumoDTO;
import com.henrique.ifconecta.domain.academico.model.Curso;
import com.henrique.ifconecta.domain.academico.port.CursoRepository;
import com.henrique.ifconecta.domain.academico.port.DisciplinaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListarDisciplinasUseCase {

    private final DisciplinaRepository disciplinaRepository;
    private final CursoRepository cursoRepository;

    @Transactional
    public List<DisciplinaResumoDTO> execute(UUID cursoIdFiltro) {
        var disciplinas = disciplinaRepository.listarTodas(cursoIdFiltro);
        if (disciplinas.isEmpty()) return List.of();

        Map<UUID, Curso> cursosPorId = cursoRepository.listarTodos().stream()
                .collect(Collectors.toMap(Curso::getId, Function.identity()));

        return disciplinas.stream().map(d -> {
            Curso c = cursosPorId.get(d.getCursoId());
            return new DisciplinaResumoDTO(
                    d.getId(),
                    d.getNome(),
                    d.getCargaHoraria(),
                    d.getCursoId(),
                    c != null ? c.getSigla() : "",
                    c != null ? c.getNome() : ""
            );
        }).collect(Collectors.toList());
    }
}
