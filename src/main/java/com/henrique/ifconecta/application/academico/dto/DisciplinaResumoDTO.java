package com.henrique.ifconecta.application.academico.dto;

import java.util.UUID;

public record DisciplinaResumoDTO(
    UUID id,
    String nome,
    int cargaHoraria,
    UUID cursoId,
    String cursoSigla,
    String cursoNome
) {
}
