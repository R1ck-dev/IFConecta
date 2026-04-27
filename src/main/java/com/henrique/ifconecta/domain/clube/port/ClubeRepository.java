package com.henrique.ifconecta.domain.clube.port;

import java.util.Optional;
import java.util.UUID;

import com.henrique.ifconecta.domain.clube.model.Clube;
import com.henrique.ifconecta.domain.shared.Pagina;

public interface ClubeRepository {
    Clube salvar(Clube clube);
    Optional<Clube> buscarPorId(UUID id);
    Pagina<Clube> listarTodosAtivos(int pagina, int tamanho);
    boolean existePorNome(String nome);
}
