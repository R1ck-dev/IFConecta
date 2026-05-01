package com.henrique.ifconecta.application.notificacao.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.henrique.ifconecta.domain.notificacao.enums.TipoAlvoComunicado;

public record NotificacaoResumoDTO(
    UUID id,
    String titulo,
    String mensagem,
    boolean lida,
    TipoAlvoComunicado tipoAlvo,
    UUID referenciaId,
    LocalDateTime dataCriacao
) {
    
}
