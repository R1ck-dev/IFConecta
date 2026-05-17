package com.henrique.ifconecta.infrastructure.web.usuario.dto;

import jakarta.validation.constraints.NotBlank;

public record AtualizarMeuPerfilRequest(
    @NotBlank(message = "O nome é obrigatório.")
    String nome
) {
}
