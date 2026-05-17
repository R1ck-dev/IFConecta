package com.henrique.ifconecta.application.usuario.dto;

import java.util.UUID;

public record AtualizarMeuPerfilInput(
    UUID usuarioId,
    String nome
) {
}
