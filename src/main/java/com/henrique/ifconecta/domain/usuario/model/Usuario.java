package com.henrique.ifconecta.domain.usuario.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.henrique.ifconecta.domain.usuario.enums.RoleUsuario;
import com.henrique.ifconecta.domain.usuario.enums.StatusUsuario;

public abstract class Usuario {
    protected UUID id;
    protected UUID cursoId;
    protected String nome;
    protected String emailAcad;
    protected String senhaHash;
    protected StatusUsuario status;
    protected RoleUsuario role;
    protected LocalDateTime dataCriacao;

    // Construtor de Criação
    protected Usuario(UUID id, UUID cursoId, String nome, String emailAcad, String senhaHash, RoleUsuario role) {
        this.id = (id != null) ? id : UUID.randomUUID();
        this.cursoId = cursoId;
        this.nome = nome;
        this.emailAcad = emailAcad;
        this.senhaHash = senhaHash;
        this.status = StatusUsuario.PENDENTE_VERIFICACAO;
        this.role = role;
        this.dataCriacao = LocalDateTime.now();
    }

    // Construtor de Reconstituição
    protected Usuario(UUID id, UUID cursoId, String nome, String emailAcad, String senhaHash, StatusUsuario status,
            RoleUsuario role, LocalDateTime dataCriacao) {
        this.id = id;
        this.cursoId = cursoId;
        this.nome = nome;
        this.emailAcad = emailAcad;
        this.senhaHash = senhaHash;
        this.status = status;
        this.role = role;
        this.dataCriacao = dataCriacao;
    }

    public void definirSenha(String novaSenhaHash) {
        this.senhaHash = novaSenhaHash;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmailAcad() {
        return emailAcad;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public StatusUsuario getStatus() {
        return status;
    }

    public RoleUsuario getRole() {
        return role;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void ativarConta() {
        this.status = StatusUsuario.ATIVO;
    }

    public UUID getCursoId() {
        return cursoId;
    }

}
