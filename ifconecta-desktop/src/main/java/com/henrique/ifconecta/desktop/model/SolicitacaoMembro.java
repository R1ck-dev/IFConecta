package com.henrique.ifconecta.desktop.model;

import java.time.LocalDateTime;

/** Espelha SolicitacaoMembroDTO (GET /api/clubes/{id}/solicitacoes). */
public record SolicitacaoMembro(
        String usuarioId,
        String usuarioNome,
        LocalDateTime dataSolicitacao) {
}
