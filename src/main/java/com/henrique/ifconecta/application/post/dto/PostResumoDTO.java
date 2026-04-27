package com.henrique.ifconecta.application.post.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostResumoDTO(
    UUID id,
    String autorNome,
    String conteudo,
    int qtdUpvotes,
    int qtdComentarios,
    LocalDateTime dataCriacao
) {
    
}
