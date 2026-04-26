package com.henrique.ifconecta.application.post.dto;

import java.util.UUID;

public record CriarPostInput(
    UUID autorId,
    UUID clubeId,
    String conteudo
) {
} 
