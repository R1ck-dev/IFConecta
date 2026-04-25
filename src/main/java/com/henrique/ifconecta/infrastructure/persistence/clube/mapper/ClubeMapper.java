package com.henrique.ifconecta.infrastructure.persistence.clube.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.clube.model.Clube;
import com.henrique.ifconecta.domain.clube.model.MembroClube;
import com.henrique.ifconecta.infrastructure.persistence.clube.entity.ClubeJpaEntity;
import com.henrique.ifconecta.infrastructure.persistence.clube.entity.MembroClubeJpaEntity;
import com.henrique.ifconecta.infrastructure.persistence.usuario.entity.UsuarioJpaEntity;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClubeMapper {
    
    private final EntityManager entityManager;

    public ClubeJpaEntity toEntity(Clube domain) {
        ClubeJpaEntity entity = new ClubeJpaEntity();

        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setDescricao(domain.getDescricao());
        entity.setStatus(domain.getStatus());
        entity.setTipoAcesso(domain.getTipoAcesso());
        entity.setDataCriacao(domain.getDataCriacao());

        entity.setMembros(domain.getMembros().stream()
                .map(membro -> toMembroEntity(membro, entity))
                .collect(Collectors.toList()));

        return entity;
    }

    public MembroClubeJpaEntity toMembroEntity(MembroClube domain, ClubeJpaEntity clubeEntity) {
        MembroClubeJpaEntity entity = new MembroClubeJpaEntity();

        entity.setId(domain.getId());
        entity.setClube(clubeEntity);
        entity.setPapel(domain.getPapel());
        entity.setStatus(domain.getStatus());
        entity.setDataIngresso(domain.getDataIngresso());

        // Obtemos uma referência "proxy" do utilizador pelo ID para não fazer SELECT desnecessário
        entity.setUsuario(entityManager.getReference(UsuarioJpaEntity.class, domain.getUsuarioId()));

        return entity;
    }

    public Clube toDomain(ClubeJpaEntity entity) {
        return new Clube(
                entity.getId(),
                entity.getNome(),
                entity.getDescricao(),
                entity.getStatus(),
                entity.getTipoAcesso(),
                entity.getDataCriacao(),
                entity.getMembros().stream()
                        .map(this::toMembroDomain)
                        .collect(Collectors.toList())
        );
    }

    private MembroClube toMembroDomain(MembroClubeJpaEntity entity) {
        return new MembroClube(
                entity.getId(),
                entity.getUsuario().getId(),
                entity.getPapel(),
                entity.getStatus(),
                entity.getDataIngresso()
        );
    }
}
