package com.henrique.ifconecta.desktop.model;

import java.time.LocalDateTime;

/** Espelha NotificacaoResumoDTO (GET /api/comunicados/minhas). */
public record NotificacaoResumo(
        String id,
        String titulo,
        String mensagem,
        boolean lida,
        String tipoAlvo,
        String referenciaId,
        LocalDateTime dataCriacao,
        String remetenteId,
        String remetenteNome) {
}
