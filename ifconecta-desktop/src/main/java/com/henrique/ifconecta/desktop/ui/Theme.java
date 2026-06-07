package com.henrique.ifconecta.desktop.ui;

import javafx.scene.Scene;

/**
 * Estilos da aplicação (tema claro).
 */
public final class Theme {

    private static final String BASE = "/com/henrique/ifconecta/desktop/css/";

    private static final String TOKENS = res("tokens.css");
    private static final String APP = res("app.css");

    private Theme() {
    }

    public static void install(Scene scene) {
        applyTo(scene);
    }

    /** Aplica os stylesheets a uma cena qualquer (ex.: janelas modais). */
    public static void applyTo(Scene target) {
        target.getStylesheets().setAll(TOKENS, APP);
    }

    private static String res(String file) {
        return Theme.class.getResource(BASE + file).toExternalForm();
    }
}
