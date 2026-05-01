package com.henrique.ifconecta.domain.academico.port;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.henrique.ifconecta.domain.academico.model.Turma;

public interface TurmaRepository {
    Turma salvar(Turma turma);
    Optional<Turma> buscarPorId(UUID id);
    List<UUID> buscarIdsAlunosMatriculados(UUID turmaId);
}
