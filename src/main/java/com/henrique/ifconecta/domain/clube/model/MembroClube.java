package com.henrique.ifconecta.domain.clube.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.henrique.ifconecta.domain.clube.enums.PapelMembro;
import com.henrique.ifconecta.domain.clube.enums.StatusMembro;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;

public class MembroClube {
    private UUID id;
    private UUID usuarioId; // No domínio, podemos guardar só o ID para reduzir o acoplamento
    private PapelMembro papel;
    private StatusMembro status;
    private LocalDateTime dataIngresso;

    // Construtor de Criação
    public MembroClube(UUID usuarioId, PapelMembro papel, StatusMembro status) {
        this.id = UUID.randomUUID();
        this.usuarioId = usuarioId;
        this.papel = papel;
        this.status = status;
        this.dataIngresso = LocalDateTime.now();
    }

    // Construtor de Reconstituição
    public MembroClube(UUID id, UUID usuarioId, PapelMembro papel, StatusMembro status, LocalDateTime dataIngresso) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.papel = papel;
        this.status = status;
        this.dataIngresso = dataIngresso;
    }

    public void aprovar() {
        if (this.status == StatusMembro.APROVADO) {
            throw new NegocioException("Este membro já está aprovado.");
        }
        this.status = StatusMembro.APROVADO;
    }

    public void rejeitar() {
        if (this.status == StatusMembro.REJEITADO) {
            throw new NegocioException("Este membro já está reprovado.");
        }
        this.status = StatusMembro.REJEITADO;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public PapelMembro getPapel() {
        return papel;
    }

    public StatusMembro getStatus() {
        return status;
    }

    public LocalDateTime getDataIngresso() {
        return dataIngresso;
    }

}
