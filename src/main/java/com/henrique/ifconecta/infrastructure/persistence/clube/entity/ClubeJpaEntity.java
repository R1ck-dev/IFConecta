package com.henrique.ifconecta.infrastructure.persistence.clube.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.henrique.ifconecta.domain.clube.enums.StatusClube;
import com.henrique.ifconecta.domain.clube.enums.TipoAcesso;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "clubes")
@Getter
@Setter
public class ClubeJpaEntity {
    @Id
    private UUID id;
    
    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusClube status;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_acesso", nullable = false)
    private TipoAcesso tipoAcesso;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @OneToMany(mappedBy = "clube", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MembroClubeJpaEntity> membros = new ArrayList<>();
}
