package com.henrique.ifconecta.desktop.model;

import java.time.LocalDateTime;

public record Comentario(
        String id,
        String autorId,
        String autorNome,
        String conteudo,
        LocalDateTime dataCriacao) {
}
