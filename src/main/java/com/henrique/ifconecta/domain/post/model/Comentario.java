package com.henrique.ifconecta.domain.post.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.henrique.ifconecta.domain.usuario.exception.NegocioException;

public class Comentario {
    private UUID id;
    private UUID autorId;
    private String conteudo;
    private LocalDateTime dataCriacao;

    // Construtor de Criação
    public Comentario(UUID autorId, String conteudo) {
        if (conteudo == null || conteudo.trim().isEmpty()) {
            throw new NegocioException("O comentário não pode estar vazio.");
        }
        this.id = UUID.randomUUID();
        this.autorId = autorId;
        this.conteudo = conteudo;
        this.dataCriacao = LocalDateTime.now();
    }

    // Construtor de Reconstituição
    public Comentario(UUID id, UUID autorId, String conteudo, LocalDateTime dataCriacao) {
        this.id = id;
        this.autorId = autorId;
        this.conteudo = conteudo;
        this.dataCriacao = dataCriacao;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAutorId() {
        return autorId;
    }

    public String getConteudo() {
        return conteudo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

}
