package com.henrique.ifconecta.desktop.service;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.henrique.ifconecta.desktop.core.http.ApiClient;
import com.henrique.ifconecta.desktop.model.Pagina;
import com.henrique.ifconecta.desktop.model.TurmaResumo;

/** Endpoints de turmas — espelha services/academico.js do front web. */
public final class TurmaService {

    private TurmaService() {
    }

    /** GET /api/turmas?pagina=&tamanho= */
    public static Pagina<TurmaResumo> listar(int pagina, int tamanho) {
        return ApiClient.get("/turmas?pagina=" + pagina + "&tamanho=" + tamanho,
                new TypeReference<Pagina<TurmaResumo>>() {
                });
    }

    /** GET /api/turmas/minhas — turmas em que o aluno está matriculado. */
    public static List<TurmaResumo> minhas() {
        return ApiClient.get("/turmas/minhas", new TypeReference<List<TurmaResumo>>() {
        });
    }

    /** GET /api/turmas/lecionadas — turmas do professor. */
    public static List<TurmaResumo> lecionadas() {
        return ApiClient.get("/turmas/lecionadas", new TypeReference<List<TurmaResumo>>() {
        });
    }

    /** GET /api/turmas/solicitacoes-leciono — solicitações pendentes para o professor aprovar. */
    public static List<TurmaResumo> solicitacoesParaAprovar() {
        return ApiClient.get("/turmas/solicitacoes-leciono", new TypeReference<List<TurmaResumo>>() {
        });
    }

    /** POST /api/turmas */
    public static void criar(String disciplinaId, String professorId, String semestre, String codigoTurma) {
        ApiClient.post("/turmas", Map.of(
                "disciplinaId", disciplinaId,
                "professorId", professorId,
                "semestre", semestre,
                "codigoTurma", codigoTurma));
    }

    /** POST /api/turmas/{id}/aprovar */
    public static void aprovar(String turmaId) {
        ApiClient.post("/turmas/" + turmaId + "/aprovar", null);
    }

    /** POST /api/turmas/{id}/rejeitar */
    public static void rejeitar(String turmaId) {
        ApiClient.post("/turmas/" + turmaId + "/rejeitar", null);
    }

    /** POST /api/turmas/{id}/matricular */
    public static void matricular(String turmaId) {
        ApiClient.post("/turmas/" + turmaId + "/matricular", null);
    }

    /** DELETE /api/turmas/{id}/matricula */
    public static void cancelarMatricula(String turmaId) {
        ApiClient.delete("/turmas/" + turmaId + "/matricula");
    }
}
