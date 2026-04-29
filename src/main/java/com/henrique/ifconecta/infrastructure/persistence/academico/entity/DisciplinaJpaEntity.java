package com.henrique.ifconecta.infrastructure.persistence.academico.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "disciplinas")
@Getter
@Setter
public class DisciplinaJpaEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private CursoJpaEntity curso;

    @Column(nullable = false)
    private String nome;

    @Column(name = "carga_horaria", nullable = false)
    private int cargaHoraria;
}
