package com.henrique.ifconecta.infrastructure.persistence.clube.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.henrique.ifconecta.domain.clube.enums.StatusClube;
import com.henrique.ifconecta.infrastructure.persistence.clube.entity.ClubeJpaEntity;

@Repository
public interface SpringDataClubeRepository extends JpaRepository<ClubeJpaEntity, UUID> {
    boolean existsByNome(String nome);

    Page<ClubeJpaEntity> findAllByStatus(StatusClube status, Pageable pageable);
}
