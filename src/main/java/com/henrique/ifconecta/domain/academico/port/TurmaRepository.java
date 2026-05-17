package com.henrique.ifconecta.domain.academico.port;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.henrique.ifconecta.domain.academico.model.Turma;
import com.henrique.ifconecta.domain.shared.Pagina;

public interface TurmaRepository {
    Turma salvar(Turma turma);
    Optional<Turma> buscarPorId(UUID id);
    List<UUID> buscarIdsAlunosMatriculados(UUID turmaId);
    Pagina<Turma> listarTodas(int pagina, int tamanho, UUID disciplinaIdFiltro, String semestreFiltro);
    List<Turma> listarMatriculadasPorAluno(UUID alunoId);
    List<Turma> listarLecionadasPorProfessor(UUID professorId);
}
