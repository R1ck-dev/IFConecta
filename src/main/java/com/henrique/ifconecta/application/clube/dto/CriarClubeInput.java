package com.henrique.ifconecta.application.clube.dto;

import java.util.UUID;

public record CriarClubeInput(
    String nome,
    String descricao,
    UUID criadorId
) {
    
}
