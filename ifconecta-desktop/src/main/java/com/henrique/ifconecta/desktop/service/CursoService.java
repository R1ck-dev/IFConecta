package com.henrique.ifconecta.desktop.service;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.henrique.ifconecta.desktop.core.http.ApiClient;
import com.henrique.ifconecta.desktop.model.Curso;
import com.henrique.ifconecta.desktop.model.DisciplinaResumo;

/** Endpoints de cursos e disciplinas — espelha services/cursos.js do front web. */
public final class CursoService {

    private CursoService() {
    }

    /** GET /api/cursos */
    public static List<Curso> listar() {
        return ApiClient.get("/cursos", new TypeReference<List<Curso>>() {
        });
    }

    /** GET /api/disciplinas */
    public static List<DisciplinaResumo> disciplinas() {
        return ApiClient.get("/disciplinas", new TypeReference<List<DisciplinaResumo>>() {
        });
    }

    /** GET /api/disciplinas?cursoId= */
    public static List<DisciplinaResumo> disciplinasDoCurso(String cursoId) {
        return ApiClient.get("/disciplinas?cursoId=" + cursoId, new TypeReference<List<DisciplinaResumo>>() {
        });
    }
}
