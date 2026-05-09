package com.henrique.ifconecta.infrastructure.web.post.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public record CriarPostRequest(
    @NotBlank(message = "O conteúdo do post não pode ser vazio.")
    String conteudo,
    UUID clubeId
) {
    
}
