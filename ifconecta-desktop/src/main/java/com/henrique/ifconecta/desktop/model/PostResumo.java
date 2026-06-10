package com.henrique.ifconecta.desktop.model;

import java.time.LocalDateTime;

/** Espelha PostResumoDTO (GET /api/posts). */
public record PostResumo(
        String id,
        String autorNome,
        String conteudo,
        int qtdUpvotes,
        boolean jaDeiUpvote,
        int qtdComentarios,
        LocalDateTime dataCriacao) {
}
