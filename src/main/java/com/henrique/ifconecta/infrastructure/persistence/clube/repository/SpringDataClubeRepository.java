package com.henrique.ifconecta.infrastructure.persistence.clube.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.henrique.ifconecta.domain.clube.enums.StatusClube;
import com.henrique.ifconecta.infrastructure.persistence.clube.entity.ClubeJpaEntity;

@Repository
public interface SpringDataClubeRepository extends JpaRepository<ClubeJpaEntity, UUID> {
    boolean existsByNome(String nome);
    List<ClubeJpaEntity> findAllByStatus(StatusClube status);
} 
