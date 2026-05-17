package com.henrique.ifconecta.infrastructure.persistence.clube.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.henrique.ifconecta.domain.clube.enums.StatusClube;
import com.henrique.ifconecta.infrastructure.persistence.clube.entity.ClubeJpaEntity;

@Repository
public interface SpringDataClubeRepository extends JpaRepository<ClubeJpaEntity, UUID> {
    boolean existsByNome(String nome);

    Page<ClubeJpaEntity> findAllByStatus(StatusClube status, Pageable pageable);

    @Query("SELECT m.usuario.id FROM MembroClubeJpaEntity m WHERE m.clube.id = :clubeId AND m.status = 'APROVADO'")
    List<UUID> findIdsMembrosAprovadosByClubeId(@Param("clubeId") UUID clubeId);

    @Query("SELECT DISTINCT c FROM ClubeJpaEntity c JOIN c.membros m WHERE m.usuario.id = :usuarioId AND m.status = 'APROVADO' AND c.status = 'ATIVO' ORDER BY c.dataCriacao DESC")
    List<ClubeJpaEntity> findClubesByMembroAprovado(@Param("usuarioId") UUID usuarioId);
}
