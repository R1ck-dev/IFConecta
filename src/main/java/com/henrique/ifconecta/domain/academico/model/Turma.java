package com.henrique.ifconecta.domain.academico.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.henrique.ifconecta.domain.academico.enums.StatusTurma;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;

public class Turma {
    private UUID id;
    private UUID disciplinaId;
    private UUID professorId;
    private String semestre;
    private String codigoTurma;
    private StatusTurma status;
    private UUID solicitanteId;
    private LocalDateTime dataCriacao;
    private Set<UUID> alunosMatriculados;

    // Construtor de criacao: status inicial vem decidido pelo use case
    public Turma(UUID disciplinaId, UUID professorId, String semestre, String codigoTurma,
            StatusTurma status, UUID solicitanteId) {
        this.id = UUID.randomUUID();
        this.disciplinaId = disciplinaId;
        this.professorId = professorId;
        this.semestre = semestre;
        this.codigoTurma = codigoTurma;
        this.status = status;
        this.solicitanteId = solicitanteId;
        this.dataCriacao = LocalDateTime.now();
        this.alunosMatriculados = new HashSet<>();
    }

    // Construtor de reconstituicao
    public Turma(UUID id, UUID disciplinaId, UUID professorId, String semestre, String codigoTurma,
            StatusTurma status, UUID solicitanteId, LocalDateTime dataCriacao, Set<UUID> alunosMatriculados) {
        this.id = id;
        this.disciplinaId = disciplinaId;
        this.professorId = professorId;
        this.semestre = semestre;
        this.codigoTurma = codigoTurma;
        this.status = status;
        this.solicitanteId = solicitanteId;
        this.dataCriacao = dataCriacao;
        this.alunosMatriculados = alunosMatriculados;
    }

    public void aprovar(UUID professorAprovadorId) {
        if (!this.professorId.equals(professorAprovadorId)) {
            throw new NegocioException("Apenas o professor responsavel pode aprovar a turma.");
        }
        if (this.status != StatusTurma.PENDENTE) {
            throw new NegocioException("Esta turma nao esta pendente de aprovacao.");
        }
        this.status = StatusTurma.ATIVA;
    }

    public void rejeitar(UUID professorAvaliadorId) {
        if (!this.professorId.equals(professorAvaliadorId)) {
            throw new NegocioException("Apenas o professor responsavel pode rejeitar a turma.");
        }
        if (this.status != StatusTurma.PENDENTE) {
            throw new NegocioException("Esta turma nao esta pendente de aprovacao.");
        }
        this.status = StatusTurma.REJEITADA;
    }

    public void matricularAluno(UUID alunoId) {
        if (this.status != StatusTurma.ATIVA) {
            throw new NegocioException("So e possivel matricular em turmas ativas.");
        }
        if (this.professorId.equals(alunoId)) {
            throw new NegocioException("O professor responsavel nao pode se matricular na propria turma.");
        }
        if (this.alunosMatriculados.contains(alunoId)) {
            throw new NegocioException("O aluno ja esta matriculado nesta turma.");
        }
        this.alunosMatriculados.add(alunoId);
    }

    public void cancelarMatricula(UUID alunoId) {
        if (!this.alunosMatriculados.contains(alunoId)) {
            throw new NegocioException("O aluno nao esta matriculado nesta turma.");
        }
        this.alunosMatriculados.remove(alunoId);
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

    public StatusTurma getStatus() {
        return status;
    }

    public UUID getSolicitanteId() {
        return solicitanteId;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public Set<UUID> getAlunosMatriculados() {
        return alunosMatriculados;
    }

}
