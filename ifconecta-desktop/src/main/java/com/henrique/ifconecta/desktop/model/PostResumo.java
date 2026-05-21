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

    /** Cópia com upvote alternado — usado para atualização otimista da UI. */
    public PostResumo comUpvoteAlternado() {
        int delta = jaDeiUpvote ? -1 : 1;
        return new PostResumo(id, autorNome, conteudo,
                Math.max(0, qtdUpvotes + delta), !jaDeiUpvote, qtdComentarios, dataCriacao);
    }
}
