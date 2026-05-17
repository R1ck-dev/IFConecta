package com.henrique.ifconecta.application.clube.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.henrique.ifconecta.domain.clube.enums.PapelMembro;

public record MembroClubeDTO(
    UUID usuarioId,
    String nome,
    String tipo,
    PapelMembro papel,
    LocalDateTime dataIngresso
) {
}
