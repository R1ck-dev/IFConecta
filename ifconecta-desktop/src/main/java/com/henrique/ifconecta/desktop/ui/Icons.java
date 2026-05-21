package com.henrique.ifconecta.desktop.ui;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.scene.paint.Color;

/** Atalhos para criar ícones Feather (equivale ao &lt;Icon&gt; do front web). */
public final class Icons {

    private Icons() {
    }

    public static FontIcon of(Ikon code, int size) {
        FontIcon icon = new FontIcon(code);
        icon.setIconSize(size);
        return icon;
    }

    public static FontIcon of(Ikon code, int size, Color color) {
        FontIcon icon = of(code, size);
        icon.setIconColor(color);
        return icon;
    }

    /** Cor definida via CSS (.ikonli-font-icon). */
    public static FontIcon of(String literal, int size) {
        FontIcon icon = new FontIcon(literal);
        icon.setIconSize(size);
        return icon;
    }

    public static FontIcon of(String literal, int size, Color color) {
        FontIcon icon = of(literal, size);
        icon.setIconColor(color);
        return icon;
    }
}
