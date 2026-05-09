package com.henrique.ifconecta.domain.clube.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.henrique.ifconecta.domain.clube.enums.PapelMembro;

public class MembroClube {
    private UUID id;
    private UUID usuarioId;
    private PapelMembro papel;
    private LocalDateTime dataIngresso;

    public MembroClube(UUID usuarioId, PapelMembro papel) {
        this.id = UUID.randomUUID();
        this.usuarioId = usuarioId;
        this.papel = papel;
        this.dataIngresso = LocalDateTime.now();
    }

    public MembroClube(UUID id, UUID usuarioId, PapelMembro papel, LocalDateTime dataIngresso) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.papel = papel;
        this.dataIngresso = dataIngresso;
    }

    public UUID getId() { return id; }
    public UUID getUsuarioId() { return usuarioId; }
    public PapelMembro getPapel() { return papel; }
    public LocalDateTime getDataIngresso() { return dataIngresso; }
}
