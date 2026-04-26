package com.henrique.ifconecta.infrastructure.web.clube.dto;

import jakarta.validation.constraints.NotNull;

public record AvaliarMembroRequest(
    @NotNull(message = "A decisão é obrigatória.")
    Boolean aprovado
) {
} 
