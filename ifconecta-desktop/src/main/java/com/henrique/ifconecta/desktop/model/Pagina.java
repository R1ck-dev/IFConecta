package com.henrique.ifconecta.desktop.model;

import java.util.List;

/** Espelha Pagina&lt;T&gt; (domain/shared/Pagina.java) — resposta paginada do backend. */
public record Pagina<T>(
        List<T> itens,
        int paginaAtual,
        int totalPaginas,
        long totalItens) {

    public boolean temProximaPagina() {
        return paginaAtual + 1 < totalPaginas;
    }
}
