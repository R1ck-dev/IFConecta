package com.henrique.ifconecta.domain.academico.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.henrique.ifconecta.domain.usuario.exception.NegocioException;

public class Turma {
    private UUID id;
    private UUID disciplinaId;
    private UUID professorId;
    private String semestre;
    private String codigoTurma;
    private LocalDateTime dataCriacao;
    private Set<UUID> alunosMatriculados;

    public Turma(UUID disciplinaId, UUID professorId, String semestre, String codigoTurma) {
        this.id = UUID.randomUUID();
        this.disciplinaId = disciplinaId;
        this.professorId = professorId;
        this.semestre = semestre;
        this.codigoTurma = codigoTurma;
        this.dataCriacao = LocalDateTime.now();
        this.alunosMatriculados = new HashSet<>();
    }

    public Turma(UUID id, UUID disciplinaId, UUID professorId, String semestre, String codigoTurma,
            LocalDateTime dataCriacao, Set<UUID> alunosMatriculados) {
        this.id = id;
        this.disciplinaId = disciplinaId;
        this.professorId = professorId;
        this.semestre = semestre;
        this.codigoTurma = codigoTurma;
        this.dataCriacao = dataCriacao;
        this.alunosMatriculados = alunosMatriculados;
    }

    public void matricularAluno(UUID alunoId) {
        if (this.alunosMatriculados.contains(alunoId)) {
            throw new NegocioException("O aluno já está matriculado nesta turma.");
        }
        this.alunosMatriculados.add(alunoId);
    }

    public UUID getId() {
        return id;
    }

    public UUID getDisciplinaId() {
        return disciplinaId;
    }

    public UUID getProfessorId() {
        return professorId;
    }

    public String getSemestre() {
        return semestre;
    }

    public String getCodigoTurma() {
        return codigoTurma;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public Set<UUID> getAlunosMatriculados() {
        return alunosMatriculados;
    }

}
