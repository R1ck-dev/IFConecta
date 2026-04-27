package com.henrique.ifconecta.infrastructure.persistence.post.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.henrique.ifconecta.infrastructure.persistence.clube.entity.ClubeJpaEntity;
import com.henrique.ifconecta.infrastructure.persistence.usuario.entity.UsuarioJpaEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "posts")
@Getter
@Setter
public class PostJpaEntity {
    @Id
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private UsuarioJpaEntity autor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clube_id")
    private ClubeJpaEntity clube;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "post_upvotes", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "usuario_id")
    private Set<UUID> upvotes = new HashSet<>();

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComentarioJpaEntity> comentarios = new ArrayList<>();
}
