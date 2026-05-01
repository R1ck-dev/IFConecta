package com.henrique.ifconecta.infrastructure.persistence.usuario.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.henrique.ifconecta.infrastructure.persistence.usuario.entity.UsuarioJpaEntity;

@Repository
public interface SpringDataUsuarioRepository extends JpaRepository<UsuarioJpaEntity, UUID> {
    Optional<UsuarioJpaEntity> findByEmailAcad(String emailAcad);

    boolean existsByEmailAcad(String emailAcad);

    @Query("SELECT u.id FROM UsuarioJpaEntity u WHERE u.status = 'ATIVO'")
    List<UUID> findAllAtivosIds();

    @Query("SELECT u.id FROM UsuarioJpaEntity u WHERE u.cursoId = :cursoId AND u.status = 'ATIVO'")
    List<UUID> findIdsByCursoId(@Param("cursoId") UUID cursoId);
}
