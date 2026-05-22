package com.henrique.ifconecta.desktop.model;

/** Espelha ClubeResumoDTO (GET /api/clubes). */
public record ClubeResumo(
        String id,
        String nome,
        String descricao,
        String tipoAcesso,
        int quantidadeMembros) {
}
