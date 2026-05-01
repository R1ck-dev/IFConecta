package com.henrique.ifconecta.domain.notificacao.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.henrique.ifconecta.domain.notificacao.enums.TipoAlvoComunicado;

public class Notificacao {
    private UUID id;
    private UUID usuarioId;
    private UUID remetenteId;
    private String titulo;
    private String mensagem;
    private boolean lida;
    private TipoAlvoComunicado tipoAlvo;
    private UUID referenciaId;
    private LocalDateTime dataCriacao;

    // Construtor de Criação
    public Notificacao(UUID usuarioId, UUID remetenteId, String titulo, String mensagem,
            TipoAlvoComunicado tipoAlvo, UUID referenciaId) {
        this.id = UUID.randomUUID();
        this.usuarioId = usuarioId;
        this.remetenteId = remetenteId;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.lida = false;
        this.tipoAlvo = tipoAlvo;
        this.referenciaId = referenciaId;
        this.dataCriacao = LocalDateTime.now();
    }

    // Construtor de Reconstituição
    public Notificacao(UUID id, UUID usuarioId, UUID remetenteId, String titulo, String mensagem,
            boolean lida, TipoAlvoComunicado tipoAlvo, UUID referenciaId, LocalDateTime dataCriacao) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.remetenteId = remetenteId;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.lida = lida;
        this.tipoAlvo = tipoAlvo;
        this.referenciaId = referenciaId;
        this.dataCriacao = dataCriacao;
    }

    public void marcarComoLida() {
        this.lida = true;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public UUID getRemetenteId() {
        return remetenteId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public boolean isLida() {
        return lida;
    }

    public TipoAlvoComunicado getTipoAlvo() {
        return tipoAlvo;
    }

    public UUID getReferenciaId() {
        return referenciaId;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

}
