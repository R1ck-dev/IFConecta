package com.henrique.ifconecta.application.post.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PostDetalheDTO(
        UUID id,
        UUID autorId,
        String autorNome,
        UUID clubeId,
        String conteudo,
        boolean anonimo,
        int qtdUpvotes,
        boolean jaDeiUpvote,
        LocalDateTime dataCriacao,
        List<ComentarioDTO> comentarios) {
}
