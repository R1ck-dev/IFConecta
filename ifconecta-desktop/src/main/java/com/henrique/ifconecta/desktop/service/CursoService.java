package com.henrique.ifconecta.desktop.service;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.henrique.ifconecta.desktop.core.http.ApiClient;
import com.henrique.ifconecta.desktop.model.Curso;

/** Endpoints de cursos — espelha services/cursos.js do front web. */
public final class CursoService {

    private CursoService() {
    }

    /** GET /api/cursos */
    public static List<Curso> listar() {
        return ApiClient.get("/cursos", new TypeReference<List<Curso>>() {
        });
    }
}
