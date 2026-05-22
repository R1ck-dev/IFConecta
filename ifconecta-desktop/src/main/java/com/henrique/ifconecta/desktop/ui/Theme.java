package com.henrique.ifconecta.desktop.ui;

import java.util.prefs.Preferences;

import javafx.scene.Scene;

/**
 * Tema claro/escuro — port do useTheme() do front web.
 * A preferência é persistida (equivale ao localStorage 'ifc-theme').
 */
public final class Theme {

    private static final Preferences PREFS = Preferences.userRoot().node("ifconecta-desktop");
    private static final String BASE = "/com/henrique/ifconecta/desktop/css/";

    private static final String TOKENS = res("tokens.css");
    private static final String TOKENS_DARK = res("tokens-dark.css");
    private static final String APP = res("app.css");

    private static Scene scene;
    private static boolean dark;

    private Theme() {
    }

    public static void install(Scene targetScene) {
        scene = targetScene;
        dark = PREFS.getBoolean("dark", false);
        apply();
    }

    public static boolean isDark() {
        return dark;
    }

    public static void toggle() {
        dark = !dark;
        PREFS.putBoolean("dark", dark);
        apply();
    }

    private static void apply() {
        if (scene != null) {
            applyTo(scene);
        }
    }

    /** Aplica os stylesheets do tema atual a uma cena qualquer (ex.: janelas modais). */
    public static void applyTo(Scene target) {
        if (dark) {
            target.getStylesheets().setAll(TOKENS, APP, TOKENS_DARK);
        } else {
            target.getStylesheets().setAll(TOKENS, APP);
        }
    }

    private static String res(String file) {
        return Theme.class.getResource(BASE + file).toExternalForm();
    }
}
