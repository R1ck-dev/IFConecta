package com.henrique.ifconecta.infrastructure.persistence.academico.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.henrique.ifconecta.infrastructure.persistence.usuario.entity.UsuarioJpaEntity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "turmas")
@Getter
@Setter
public class TurmaJpaEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disciplina_id", nullable = false)
    private DisciplinaJpaEntity disciplina;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    private UsuarioJpaEntity professor;

    @Column(nullable = false)
    private String semestre;

    @Column(name = "codigo_turma", nullable = false)
    private String codigoTurma;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "matriculas_turma", joinColumns = @JoinColumn(name = "turma_id"))
    @Column(name = "aluno_id")
    private Set<UUID> alunosMatriculados = new HashSet<>();
}
