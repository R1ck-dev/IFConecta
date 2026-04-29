package com.henrique.ifconecta.infrastructure.persistence.academico.entity;

import java.util.UUID;

import com.henrique.ifconecta.domain.academico.enums.ModalidadeCurso;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cursos")
@Getter
@Setter
public class CursoJpaEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false, unique = true)
    private String sigla;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModalidadeCurso modalidade;
}
