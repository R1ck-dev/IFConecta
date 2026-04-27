package com.henrique.ifconecta.domain.post.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.henrique.ifconecta.domain.usuario.exception.NegocioException;

public class Post {
    private UUID id;
    private UUID autorId;
    private UUID clubeId; // Se for null, o post pertence à timeline geral
    private String conteudo;
    private Set<UUID> upvotes;
    private LocalDateTime dataCriacao;
    private List<Comentario> comentarios;

    // Construtor de Criação
    public Post(UUID autorId, UUID clubeId, String conteudo) {
        if (conteudo == null | conteudo.trim().isEmpty()) {
            throw new NegocioException("O conteúdo do post não pode estar vazio.");
        }

        if (conteudo.length() > 1000) {
            throw new NegocioException("O post excedeu o limite de caracteres.");
        }

        this.id = UUID.randomUUID();
        this.autorId = autorId;
        this.clubeId = clubeId;
        this.conteudo = conteudo;
        this.upvotes = new HashSet<>();
        this.dataCriacao = LocalDateTime.now();
        this.comentarios = new ArrayList<>();
    }

    // Construtor de Reconstituição
    public Post(UUID id, UUID autorId, UUID clubeId, String conteudo, Set<UUID> upvotes, LocalDateTime dataCriacao,
            List<Comentario> comentarios) {
        this.id = id;
        this.autorId = autorId;
        this.clubeId = clubeId;
        this.conteudo = conteudo;
        this.upvotes = (upvotes != null) ? upvotes : new HashSet<>();
        this.dataCriacao = dataCriacao;
        this.comentarios = (comentarios != null) ? comentarios : new ArrayList<>();
    }

    public void adicionarComentario(UUID autorId, String conteudo) {
        Comentario novoComentario = new Comentario(autorId, conteudo);
        this.comentarios.add(novoComentario);
    }

    public void darUpVote(UUID usuarioId) {
        if (this.upvotes.contains(usuarioId)) {
            this.upvotes.remove(usuarioId);
        } else {
            this.upvotes.add(usuarioId);
        }
    }

    public int getQtdUpVotes() {
        return this.upvotes.size();
    }

    public UUID getId() {
        return id;
    }

    public UUID getAutorId() {
        return autorId;
    }

    public UUID getClubeId() {
        return clubeId;
    }

    public String getConteudo() {
        return conteudo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public Set<UUID> getUpvotes() {
        return upvotes;
    }

}
