package com.henrique.ifconecta.application.clube.dto;

import java.util.UUID;

public record ClubeResumoDTO(
    UUID id,
    String nome,
    String descricao,
    int quantidadeMembros
) {
}
