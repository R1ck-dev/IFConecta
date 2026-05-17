package com.henrique.ifconecta.application.clube.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SolicitacaoMembroDTO(
    UUID usuarioId,
    String usuarioNome,
    LocalDateTime dataSolicitacao
) {
}
