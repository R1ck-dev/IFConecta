package com.henrique.ifconecta.infrastructure.persistence.usuario.mapper;

import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.usuario.model.Usuario;
import com.henrique.ifconecta.infrastructure.persistence.usuario.entity.UsuarioJpaEntity;

@Component
public class UsuarioMapper {
    
    // Domain -> JPA
    public UsuarioJpaEntity toEntity(Usuario usuario) {
        UsuarioJpaEntity entity = new UsuarioJpaEntity();
        entity.setId(usuario.getId());
        entity.setNome(usuario.getNome());
        entity.setEmailAcad(usuario.getEmailAcad());
        entity.setSenhaHash(usuario.getSenhaHash());
        entity.setStatus(usuario.getStatus());
        entity.setDataCriacao(usuario.getDataCriacao());
        entity.setProntuario(usuario.getProntuario());
        return entity;
    }

    // JPA -> Domain
    public Usuario toDomain(UsuarioJpaEntity entity) {
        return new Usuario(
            entity.getId(),
            entity.getNome(),
            entity.getEmailAcad(),
            entity.getSenhaHash(),
            entity.getStatus(),
            entity.getDataCriacao(),
            entity.getProntuario()
        );
    }
}