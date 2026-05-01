package com.henrique.ifconecta.infrastructure.web.usuario.dto;

import jakarta.validation.constraints.NotBlank;

public record DefinirSenhaRequest(
    @NotBlank
    String token,
    @NotBlank
    String novaSenha
) {
    
}
