package com.henrique.ifconecta.application.academico.dto;

import java.util.UUID;

public record CriarTurmaInput(
        UUID solicitanteId,
        UUID disciplinaId,
        UUID professorId,
        String semestre,
        String codigoTurma) {

}
