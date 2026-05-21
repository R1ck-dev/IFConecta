package com.henrique.ifconecta.desktop.ui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Notificações toast — port do useToast()/ToastProvider do front web.
 * Renderiza num overlay (canto inferior direito) instalado por IFConectaApp.
 */
public final class Toast {

    private static VBox layer;

    private Toast() {
    }

    public static void install(VBox toastLayer) {
        layer = toastLayer;
    }

    public static void success(String title) {
        show(title, null, "toast-success");
    }

    public static void success(String title, String message) {
        show(title, message, "toast-success");
    }

    public static void error(String title) {
        show(title, null, "toast-error");
    }

    public static void error(String title, String message) {
        show(title, message, "toast-error");
    }

    public static void info(String title, String message) {
        show(title, message, null);
    }

    private static void show(String title, String message, String accentClass) {
        if (layer == null) {
            return;
        }
        VBox card = new VBox(2);
        card.getStyleClass().add("toast");
        if (accentClass != null) {
            card.getStyleClass().add(accentClass);
        }

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("toast-title");
        titleLabel.setWrapText(true);
        card.getChildren().add(titleLabel);

        if (message != null && !message.isBlank()) {
            Label messageLabel = new Label(message);
            messageLabel.getStyleClass().add("toast-msg");
            messageLabel.setWrapText(true);
            card.getChildren().add(messageLabel);
        }

        layer.getChildren().add(card);

        card.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(160), card);
        fadeIn.setToValue(1);
        fadeIn.play();

        PauseTransition wait = new PauseTransition(Duration.millis(3800));
        wait.setOnFinished(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(240), card);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(ev -> layer.getChildren().remove(card));
            fadeOut.play();
        });
        wait.play();
    }
}
