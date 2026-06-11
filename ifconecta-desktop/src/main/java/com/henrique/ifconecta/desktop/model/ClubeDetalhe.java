package com.henrique.ifconecta.desktop.model;

public record ClubeDetalhe(
        String id,
        String nome,
        String descricao,
        String tipoAcesso,
        int quantidadeMembros,
        String liderId,
        String liderNome,
        boolean souMembro,
        boolean souLider,
        String minhaSituacao) {

    public boolean temSolicitacaoPendente() {
        return "PENDENTE".equalsIgnoreCase(minhaSituacao);
    }
}
