package com.henrique.ifconecta.infrastructure.persistence.notificacao.mapper;

import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.notificacao.model.Notificacao;
import com.henrique.ifconecta.infrastructure.persistence.notificacao.entity.NotificacaoJpaEntity;
import com.henrique.ifconecta.infrastructure.persistence.usuario.entity.UsuarioJpaEntity;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificacaoMapper {

    private final EntityManager entityManager;

    public NotificacaoJpaEntity toEntity(Notificacao domain) {
        NotificacaoJpaEntity entity = new NotificacaoJpaEntity();
        
        entity.setId(domain.getId());
        entity.setTitulo(domain.getTitulo());
        entity.setMensagem(domain.getMensagem());
        entity.setLida(domain.isLida());
        entity.setTipoAlvo(domain.getTipoAlvo());
        entity.setReferenciaId(domain.getReferenciaId());
        entity.setDataCriacao(domain.getDataCriacao());

        entity.setUsuario(entityManager.getReference(UsuarioJpaEntity.class, domain.getUsuarioId()));
        entity.setRemetente(entityManager.getReference(UsuarioJpaEntity.class, domain.getRemetenteId()));

        return entity;
    }

    public Notificacao toDomain(NotificacaoJpaEntity entity) {
        return new Notificacao(
                entity.getId(),
                entity.getUsuario().getId(),
                entity.getRemetente().getId(),
                entity.getTitulo(),
                entity.getMensagem(),
                entity.isLida(),
                entity.getTipoAlvo(),
                entity.getReferenciaId(),
                entity.getDataCriacao()
        );
    }
}
