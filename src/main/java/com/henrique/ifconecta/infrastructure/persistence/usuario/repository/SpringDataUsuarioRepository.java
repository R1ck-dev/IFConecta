package com.henrique.ifconecta.infrastructure.persistence.usuario.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.henrique.ifconecta.infrastructure.persistence.usuario.entity.UsuarioJpaEntity;

@Repository
public interface SpringDataUsuarioRepository extends JpaRepository<UsuarioJpaEntity, UUID>{
    Optional<UsuarioJpaEntity> findByEmailAcad(String emailAcad);
    boolean existsByEmailAcad(String emailAcad);
} 
    