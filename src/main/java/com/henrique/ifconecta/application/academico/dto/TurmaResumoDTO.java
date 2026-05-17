package com.henrique.ifconecta.application.academico.dto;

import java.util.UUID;

public record TurmaResumoDTO(
    UUID id,
    String codigoTurma,
    String semestre,
    UUID disciplinaId,
    String disciplinaNome,
    int cargaHoraria,
    UUID professorId,
    String professorNome,
    UUID cursoId,
    String cursoSigla,
    String cursoNome,
    int qtdMatriculados,
    String status,
    UUID solicitanteId,
    String solicitanteNome
) {
}
