package com.henrique.ifconecta.application.notificacao.dto;

import java.util.UUID;

import com.henrique.ifconecta.domain.notificacao.enums.TipoAlvoComunicado;

public record EnviarComunicadoInput(
        UUID remetenteId,
        String titulo,
        String mensagem,
        TipoAlvoComunicado tipoAlvo,
        UUID alvoId 
) {

}
