package com.henrique.ifconecta.desktop.model;

public record ClubeResumo(
        String id,
        String nome,
        String descricao,
        String tipoAcesso,
        int quantidadeMembros) {
}
