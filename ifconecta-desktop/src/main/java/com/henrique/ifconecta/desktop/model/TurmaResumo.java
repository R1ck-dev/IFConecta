package com.henrique.ifconecta.desktop.model;

/** Espelha TurmaResumoDTO (GET /api/turmas). */
public record TurmaResumo(
        String id,
        String codigoTurma,
        String semestre,
        String disciplinaId,
        String disciplinaNome,
        int cargaHoraria,
        String professorId,
        String professorNome,
        String cursoId,
        String cursoSigla,
        String cursoNome,
        int qtdMatriculados,
        String status,
        String solicitanteId,
        String solicitanteNome) {

    public boolean isSolicitacao() {
        return "SOLICITACAO".equalsIgnoreCase(status);
    }

    public boolean isAtiva() {
        return "ATIVA".equalsIgnoreCase(status);
    }
}
