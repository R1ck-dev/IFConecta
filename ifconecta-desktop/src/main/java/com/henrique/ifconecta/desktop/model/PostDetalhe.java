package com.henrique.ifconecta.desktop.model;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetalhe(
        String id,
        String autorId,
        String autorNome,
        String clubeId,
        String conteudo,
        boolean anonimo,
        int qtdUpvotes,
        boolean jaDeiUpvote,
        LocalDateTime dataCriacao,
        List<Comentario> comentarios) {
}
