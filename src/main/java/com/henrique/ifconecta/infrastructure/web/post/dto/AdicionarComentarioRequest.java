package com.henrique.ifconecta.infrastructure.web.post.dto;

import jakarta.validation.constraints.NotBlank;

public record AdicionarComentarioRequest(
    @NotBlank(message = "O comentário não pode ser vazio.")
    String conteudo
) {
    
}
