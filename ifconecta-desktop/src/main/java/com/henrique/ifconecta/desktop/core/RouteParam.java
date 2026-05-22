package com.henrique.ifconecta.desktop.core;

/** Implementado por controllers de tela que recebem um argumento na navegação. */
public interface RouteParam {

    /** Chamado pelo Router logo após carregar a tela, com o argumento passado em navigate(). */
    void aoNavegar(Object argumento);
}
