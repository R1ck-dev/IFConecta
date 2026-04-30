package com.henrique.ifconecta.infrastructure.web.academico.Turma.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarTurmaRequest(
    @NotNull(message = "A disciplina é obrigatória.")
    UUID disciplinaId,
    
    @NotNull(message = "O professor é obrigatório.")
    UUID professorId,
    
    @NotBlank(message = "O semestre é obrigatório.")
    String semestre,
    
    @NotBlank(message = "O código da turma é obrigatório.")
    String codigoTurma
) {
} 
