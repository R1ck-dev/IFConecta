package com.henrique.ifconecta.desktop.model;

import java.time.LocalDateTime;

public record SolicitacaoMembro(
        String usuarioId,
        String usuarioNome,
        LocalDateTime dataSolicitacao) {
}
