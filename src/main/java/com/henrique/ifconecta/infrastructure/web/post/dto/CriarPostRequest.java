package com.henrique.ifconecta.infrastructure.web.post.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public record CriarPostRequest(
    @NotBlank(message = "O conteúdo do post não pode ser vazio.")
    String conteudo,
    @NotBlank(message = "O id do clube não pode estar vazio.")
    UUID clubeId
) {
    
}
