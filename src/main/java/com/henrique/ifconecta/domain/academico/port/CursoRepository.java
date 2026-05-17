package com.henrique.ifconecta.domain.academico.port;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.henrique.ifconecta.domain.academico.model.Curso;

public interface CursoRepository {
    Curso salvar(Curso curso);
    Optional<Curso> buscarPorId(UUID id);
    boolean existePorId(UUID id);
    List<Curso> listarTodos();
}
