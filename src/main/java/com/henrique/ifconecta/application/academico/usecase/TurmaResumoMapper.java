package com.henrique.ifconecta.application.academico.usecase;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.henrique.ifconecta.application.academico.dto.TurmaResumoDTO;
import com.henrique.ifconecta.domain.academico.model.Curso;
import com.henrique.ifconecta.domain.academico.model.Disciplina;
import com.henrique.ifconecta.domain.academico.model.Turma;
import com.henrique.ifconecta.domain.academico.port.CursoRepository;
import com.henrique.ifconecta.domain.academico.port.DisciplinaRepository;
import com.henrique.ifconecta.domain.usuario.model.Usuario;
import com.henrique.ifconecta.domain.usuario.port.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TurmaResumoMapper {

    private final DisciplinaRepository disciplinaRepository;
    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;

    public List<TurmaResumoDTO> montar(List<Turma> turmas) {
        if (turmas == null || turmas.isEmpty()) return List.of();

        Set<UUID> disciplinaIds = turmas.stream().map(Turma::getDisciplinaId).collect(Collectors.toSet());
        Set<UUID> usuarioIds = turmas.stream()
                .flatMap(t -> t.getSolicitanteId() != null
                        ? java.util.stream.Stream.of(t.getProfessorId(), t.getSolicitanteId())
                        : java.util.stream.Stream.of(t.getProfessorId()))
                .collect(Collectors.toSet());

        Map<UUID, Disciplina> disciplinas = disciplinaRepository.buscarPorIds(disciplinaIds).stream()
                .collect(Collectors.toMap(Disciplina::getId, Function.identity()));

        Set<UUID> cursoIds = disciplinas.values().stream()
                .map(Disciplina::getCursoId)
                .collect(Collectors.toSet());

        Map<UUID, Curso> cursos = cursoRepository.listarTodos().stream()
                .filter(c -> cursoIds.contains(c.getId()))
                .collect(Collectors.toMap(Curso::getId, Function.identity()));

        Map<UUID, Usuario> usuarios = usuarioRepository.buscarPorIds(usuarioIds).stream()
                .collect(Collectors.toMap(Usuario::getId, Function.identity()));

        return turmas.stream().map(turma -> {
            Disciplina disciplina = disciplinas.get(turma.getDisciplinaId());
            Curso curso = disciplina != null ? cursos.get(disciplina.getCursoId()) : null;
            Usuario professor = usuarios.get(turma.getProfessorId());
            Usuario solicitante = turma.getSolicitanteId() != null ? usuarios.get(turma.getSolicitanteId()) : null;

            return new TurmaResumoDTO(
                    turma.getId(),
                    turma.getCodigoTurma(),
                    turma.getSemestre(),
                    turma.getDisciplinaId(),
                    disciplina != null ? disciplina.getNome() : "—",
                    disciplina != null ? disciplina.getCargaHoraria() : 0,
                    turma.getProfessorId(),
                    professor != null ? professor.getNome() : "—",
                    curso != null ? curso.getId() : null,
                    curso != null ? curso.getSigla() : "",
                    curso != null ? curso.getNome() : "",
                    turma.getAlunosMatriculados() != null ? turma.getAlunosMatriculados().size() : 0,
                    turma.getStatus() != null ? turma.getStatus().name() : null,
                    turma.getSolicitanteId(),
                    solicitante != null ? solicitante.getNome() : null
            );
        }).collect(Collectors.toList());
    }
}
