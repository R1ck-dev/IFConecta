package com.henrique.ifconecta.infrastructure.persistence.post.mapper;

import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.post.model.Comentario;
import com.henrique.ifconecta.domain.post.model.Post;
import com.henrique.ifconecta.infrastructure.persistence.clube.entity.ClubeJpaEntity;
import com.henrique.ifconecta.infrastructure.persistence.post.entity.ComentarioJpaEntity;
import com.henrique.ifconecta.infrastructure.persistence.post.entity.PostJpaEntity;
import com.henrique.ifconecta.infrastructure.persistence.usuario.entity.UsuarioJpaEntity;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final EntityManager entityManager;

    public PostJpaEntity toEntity(Post domain) {
        PostJpaEntity entity = new PostJpaEntity();
        entity.setId(domain.getId());
        entity.setConteudo(domain.getConteudo());
        entity.setUpvotes(new HashSet<>(domain.getUpvotes()));
        entity.setDataCriacao(domain.getDataCriacao());

        entity.setAutor(entityManager.getReference(UsuarioJpaEntity.class, domain.getAutorId()));

        if (domain.getClubeId() != null) {
            entity.setClube(entityManager.getReference(ClubeJpaEntity.class, domain.getClubeId()));
        }

        entity.setComentarios(domain.getComentarios().stream()
                .map(c -> toComentarioEntity(c, entity))
                .collect(Collectors.toList()));

        return entity;
    }

    private ComentarioJpaEntity toComentarioEntity(Comentario domain, PostJpaEntity postEntity) {
        ComentarioJpaEntity entity = new ComentarioJpaEntity();
        entity.setId(domain.getId());
        entity.setConteudo(domain.getConteudo());
        entity.setDataCriacao(domain.getDataCriacao());
        entity.setPost(postEntity);
        entity.setAutor(entityManager.getReference(UsuarioJpaEntity.class, domain.getAutorId()));
        return entity;
    }

    public Post toDomain(PostJpaEntity entity) {
        UUID clubeId = entity.getClube() != null ? entity.getClube().getId() : null;

        return new Post(
                entity.getId(),
                entity.getAutor().getId(),
                clubeId,
                entity.getConteudo(),
                new HashSet<>(entity.getUpvotes()),
                entity.getDataCriacao(),
                entity.getComentarios().stream()
                        .map(this::toComentarioDomain)
                        .collect(Collectors.toList()));
    }

    private Comentario toComentarioDomain(ComentarioJpaEntity entity) {
        return new Comentario(
                entity.getId(),
                entity.getAutor().getId(),
                entity.getConteudo(),
                entity.getDataCriacao());
    }
}
