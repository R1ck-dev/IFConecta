package com.henrique.ifconecta.application.clube.dto;

import java.util.UUID;

import com.henrique.ifconecta.domain.clube.enums.TipoAcesso;

public record ClubeResumoDTO(
    UUID id,
    String nome,
    String descricao,
    TipoAcesso tipoAcesso,
    int quantidadeMembros
) {
} 
