package com.henrique.ifconecta.desktop.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Avatar com iniciais coloridas — port do componente &lt;Avatar&gt; do front web.
 * A cor é derivada do nome (mesma paleta e hash do web) para ser estável.
 */
public class Avatar extends StackPane {

    private static final String[][] COLORS = {
            {"#3b82f6", "#dbeafe"},
            {"#10b981", "#d1fae5"},
            {"#f59e0b", "#fef3c7"},
            {"#ef4444", "#fee2e2"},
            {"#8b5cf6", "#ede9fe"},
            {"#ec4899", "#fce7f3"},
            {"#06b6d4", "#cffafe"},
            {"#f97316", "#ffedd5"},
    };

    public Avatar(String name, double size) {
        String safe = (name == null) ? "" : name.trim();
        String key = safe.isEmpty() ? "?" : safe;
        String[] pair = COLORS[Math.floorMod(hash(key), COLORS.length)];

        Circle background = new Circle(size / 2.0, Color.web(pair[1]));

        Label initials = new Label(initials(safe));
        initials.setTextFill(Color.web(pair[0]));
        initials.setStyle("-fx-font-weight: bold; -fx-font-size: " + Math.round(size * 0.4) + "px;");

        getChildren().addAll(background, initials);
        setMinSize(size, size);
        setPrefSize(size, size);
        setMaxSize(size, size);
    }

    private static int hash(String s) {
        int h = 0;
        for (int i = 0; i < s.length(); i++) {
            h = (h << 5) - h + s.charAt(i);
        }
        return h;
    }

    private static String initials(String name) {
        if (name.isEmpty()) {
            return "?";
        }
        StringBuilder sb = new StringBuilder();
        for (String word : name.split("\\s+")) {
            if (!word.isEmpty() && sb.length() < 2) {
                sb.append(Character.toUpperCase(word.charAt(0)));
            }
        }
        return sb.length() == 0 ? "?" : sb.toString();
    }
}
