package com.henrique.ifconecta.domain.usuario.model;

import java.time.LocalDateTime;
import java.util.UUID;
import com.henrique.ifconecta.domain.usuario.enums.StatusUsuario;

public class Usuario {
    private UUID id;
    private String nome;
    private String emailAcad;
    private String senhaHash;
    private StatusUsuario status;
    private LocalDateTime dataCriacao;
    private String prontuario;

    // Construtor de Criação
    public Usuario(UUID id, String nome, String emailAcad, String senhaHash, String prontuario) {
        this.id = (id != null) ? id : UUID.randomUUID();
        this.nome = nome;
        this.emailAcad = emailAcad;
        this.senhaHash = senhaHash;
        this.status = StatusUsuario.PENDENTE_VERIFICACAO;
        this.dataCriacao = LocalDateTime.now();
        this.prontuario = prontuario;
    }

    // Construtor de Reconstituição
    public Usuario(UUID id, String nome, String emailAcad, String senhaHash, StatusUsuario status, LocalDateTime dataCriacao, String prontuario) {
        this.id = id;
        this.nome = nome;
        this.emailAcad = emailAcad;
        this.senhaHash = senhaHash;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.prontuario = prontuario;
    }

    public void ativarConta() {
        this.status = StatusUsuario.ATIVO;
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getEmailAcad() { return emailAcad; }
    public String getSenhaHash() { return senhaHash; }
    public StatusUsuario getStatus() { return status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public String getProntuario() { return prontuario; }
}