package com.henrique.ifconecta.application.academico.dto;

import java.util.List;

import com.henrique.ifconecta.domain.academico.enums.ModalidadeCurso;

public record CriarCursoInput(
        String nome,
        String sigla,
        ModalidadeCurso modalidade,
        List<DisciplinaInput> disciplinas) {
    public record DisciplinaInput(
            String nome,
            int cargaHoraria) {
    }
}
