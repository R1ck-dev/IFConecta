package com.henrique.ifconecta.application.clube.dto;

import java.util.UUID;

import com.henrique.ifconecta.domain.clube.enums.TipoAcesso;

public record CriarClubeInput(
    String nome,
    String descricao,
    TipoAcesso tipoAcesso,
    UUID criadorId
) {
    
}
