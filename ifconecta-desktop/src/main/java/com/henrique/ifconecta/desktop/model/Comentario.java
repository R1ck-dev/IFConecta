package com.henrique.ifconecta.desktop.model;

import java.time.LocalDateTime;

/** Espelha ComentarioDTO (parte de PostDetalheDTO). */
public record Comentario(
        String id,
        String autorId,
        String autorNome,
        String conteudo,
        LocalDateTime dataCriacao) {
}
