package com.henrique.ifconecta.domain.usuario.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.henrique.ifconecta.domain.usuario.exception.NegocioException;

public class TokenVerificacao {
    private UUID id;
    private Usuario usuario;
    private String token;
    private LocalDateTime dataExpiracao;
    private boolean utilizado;

    // Construtor de Criação
    public TokenVerificacao(Usuario usuario) {
        this.id = UUID.randomUUID();
        this.usuario = usuario;
        this.token = UUID.randomUUID().toString();
        this.dataExpiracao = LocalDateTime.now().plusHours(24);
        this.utilizado = false;
    }

    // Token de Reconstituição
    public TokenVerificacao(UUID id, Usuario usuario, String token, LocalDateTime dataExpiracao, boolean utilizado) {
        this.id = id;
        this.usuario = usuario;
        this.token = token;
        this.dataExpiracao = dataExpiracao;
        this.utilizado = utilizado;
    }

    public void validar() {
        if (this.utilizado) {
            throw new NegocioException("Este link de verificação já foi utilizado.");
        }

        if (LocalDateTime.now().isAfter(this.dataExpiracao)) {
            throw new NegocioException("Este link de verificação expirou. Solicite um novo");
        }

    }

    public void marcarComoUtilziado() {
        this.utilizado = true;
    }

    public UUID getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getDataExpiracao() {
        return dataExpiracao;
    }

    public boolean isUtilizado() {
        return utilizado;
    }

}
