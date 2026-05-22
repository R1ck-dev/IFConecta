package com.henrique.ifconecta.desktop.model;

/** Espelha DisciplinaResumoDTO (GET /api/disciplinas). */
public record DisciplinaResumo(
        String id,
        String nome,
        int cargaHoraria,
        String cursoId,
        String cursoSigla,
        String cursoNome) {
}
