package com.henrique.ifconecta.domain.academico.port;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.henrique.ifconecta.domain.academico.model.Disciplina;

public interface DisciplinaRepository {
    Disciplina salvar(Disciplina disciplina);
    List<Disciplina> listarTodas(UUID cursoIdFiltro);
    List<Disciplina> buscarPorIds(Collection<UUID> ids);
}
