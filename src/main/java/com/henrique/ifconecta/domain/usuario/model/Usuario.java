package com.henrique.ifconecta.domain.usuario.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.henrique.ifconecta.domain.usuario.enums.RoleUsuario;
import com.henrique.ifconecta.domain.usuario.enums.StatusUsuario;

public class Usuario {
    protected UUID id;
    protected String nome;
    protected String emailAcad;
    protected String senhaHash;
    protected StatusUsuario status;
    protected RoleUsuario role;
    protected LocalDateTime dataCriacao;
    protected String prontuario;

    public Usuario(UUID id, String nome, String emailAcad, String senhaHash, String prontuario) {
        this.id = (id != null) ? id : UUID.randomUUID();
        this.nome = nome;
        this.emailAcad = emailAcad;
        this.senhaHash = senhaHash;
        this.status = StatusUsuario.PENDENTE_VERIFICACAO;
        this.role = RoleUsuario.ALUNO;
        this.dataCriacao = LocalDateTime.now();
        this.prontuario = prontuario;
    }

    public Usuario(UUID id, String nome, String emailAcad, String senhaHash, StatusUsuario status,
            RoleUsuario role, LocalDateTime dataCriacao, String prontuario) {
        this.id = id;
        this.nome = nome;
        this.emailAcad = emailAcad;
        this.senhaHash = senhaHash;
        this.status = status;
        this.role = role;
        this.dataCriacao = dataCriacao;
        this.prontuario = prontuario;
    }

    public void definirSenha(String novaSenhaHash) { this.senhaHash = novaSenhaHash; }
    public void promoverParaAdmin() { this.role = RoleUsuario.ADMIN; }
    public void ativarConta() { this.status = StatusUsuario.ATIVO; }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getEmailAcad() { return emailAcad; }
    public String getSenhaHash() { return senhaHash; }
    public StatusUsuario getStatus() { return status; }
    public RoleUsuario getRole() { return role; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public String getProntuario() { return prontuario; }
}
