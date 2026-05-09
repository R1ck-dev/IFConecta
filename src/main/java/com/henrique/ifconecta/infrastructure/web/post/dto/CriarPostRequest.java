package com.henrique.ifconecta.infrastructure.web.post.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarPostRequest(
    @NotBlank(message = "O conteúdo do post não pode ser vazio.")
    String conteudo,
    @NotNull(message = "O clube do post é obrigatório.")
    UUID clubeId
) {
    
}
