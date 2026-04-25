package com.henrique.ifconecta.domain.clube.port;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.henrique.ifconecta.domain.clube.model.Clube;

public interface ClubeRepository {
    Clube salvar(Clube clube);
    Optional<Clube> buscarPorId(UUID id);
    List<Clube> listarTodosAtivos();
    boolean existePorNome(String nome);
}
