package com.henrique.ifconecta.application.post.dto;

import java.util.UUID;

public record AdicionarComentarioInput(
    UUID postId,
    UUID autorId,
    String conteudo
) {
    
}
