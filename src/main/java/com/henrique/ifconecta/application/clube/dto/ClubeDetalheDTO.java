package com.henrique.ifconecta.application.clube.dto;

import java.util.UUID;

import com.henrique.ifconecta.domain.clube.enums.StatusMembro;
import com.henrique.ifconecta.domain.clube.enums.TipoAcesso;

public record ClubeDetalheDTO(
    UUID id,
    String nome,
    String descricao,
    TipoAcesso tipoAcesso,
    int quantidadeMembros,
    UUID liderId,
    String liderNome,
    boolean souMembro,
    boolean souLider,
    StatusMembro minhaSituacao
) {
}
