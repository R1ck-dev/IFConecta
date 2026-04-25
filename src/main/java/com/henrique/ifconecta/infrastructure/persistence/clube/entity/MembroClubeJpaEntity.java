package com.henrique.ifconecta.infrastructure.persistence.clube.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.henrique.ifconecta.domain.clube.enums.PapelMembro;
import com.henrique.ifconecta.domain.clube.enums.StatusMembro;
import com.henrique.ifconecta.infrastructure.persistence.usuario.entity.UsuarioJpaEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "membros_clube")
@Getter
@Setter
public class MembroClubeJpaEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clube_id", nullable = false)
    private ClubeJpaEntity clube;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioJpaEntity usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PapelMembro papel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMembro status;

    @Column(name = "data_ingresso", nullable = false, updatable = false)
    private LocalDateTime dataIngresso; 
}
