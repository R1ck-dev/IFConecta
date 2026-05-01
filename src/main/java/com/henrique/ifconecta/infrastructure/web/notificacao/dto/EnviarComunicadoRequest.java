package com.henrique.ifconecta.infrastructure.web.notificacao.dto;

import java.util.UUID;

import com.henrique.ifconecta.domain.notificacao.enums.TipoAlvoComunicado;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EnviarComunicadoRequest(
    
    @NotBlank(message = "O título do comunicado é obrigatório.")
    @Size(max = 100, message = "O título não pode ter mais de 100 caracteres.")
    String titulo,

    @NotBlank(message = "A mensagem do comunicado é obrigatória.")
    String mensagem,

    @NotNull(message = "O tipo de alvo é obrigatório (GERAL, CURSO, TURMA, CLUBE).")
    TipoAlvoComunicado tipoAlvo,

    UUID alvoId 
) {}
