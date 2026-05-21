package com.henrique.ifconecta.desktop.model;

/** Espelha CursoResumoDTO (GET /api/cursos). */
public record Curso(String id, String nome, String sigla, String modalidade) {
}
