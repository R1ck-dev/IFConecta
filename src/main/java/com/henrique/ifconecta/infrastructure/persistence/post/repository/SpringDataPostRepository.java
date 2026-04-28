package com.henrique.ifconecta.infrastructure.persistence.post.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.henrique.ifconecta.infrastructure.persistence.post.entity.PostJpaEntity;

@Repository
public interface SpringDataPostRepository extends JpaRepository<PostJpaEntity, UUID> {
    @EntityGraph(attributePaths = {"autor"})
    Page<PostJpaEntity> findAllByClubeIsNullOrderByDataCriacaoDesc(Pageable pageable);
    
}
