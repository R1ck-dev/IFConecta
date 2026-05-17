package com.henrique.ifconecta.application.academico.dto;

import java.util.UUID;

import com.henrique.ifconecta.domain.academico.enums.ModalidadeCurso;

public record CursoResumoDTO(
        UUID id,
        String nome,
        String sigla,
        ModalidadeCurso modalidade) {
}
