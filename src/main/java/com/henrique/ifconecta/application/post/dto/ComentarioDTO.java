package com.henrique.ifconecta.application.post.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ComentarioDTO(
        UUID id,
        UUID autorId,
        String autorNome,
        String conteudo,
        LocalDateTime dataCriacao) {
}
