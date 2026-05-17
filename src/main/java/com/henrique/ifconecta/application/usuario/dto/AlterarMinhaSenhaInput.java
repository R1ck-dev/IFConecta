package com.henrique.ifconecta.application.usuario.dto;

import java.util.UUID;

public record AlterarMinhaSenhaInput(
    UUID usuarioId,
    String senhaAtual,
    String novaSenha
) {
}
