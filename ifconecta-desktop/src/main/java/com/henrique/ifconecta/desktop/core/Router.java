package com.henrique.ifconecta.desktop.core;

import com.henrique.ifconecta.desktop.controller.AppShellController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Navegação entre telas — substitui o React Router do front web.
 *
 * <p>Telas de autenticação (login) ocupam a janela inteira. Telas internas
 * são carregadas dentro do AppShell (header + sidebar + área central).</p>
 */
public final class Router {

    private static Router instance;

    private final Stage stage;
    private final StackPane contentLayer;
    private AppShellController shell;

    private Router(Stage stage, StackPane contentLayer) {
        this.stage = stage;
        this.contentLayer = contentLayer;
    }

    public static void init(Stage stage, StackPane contentLayer) {
        instance = new Router(stage, contentLayer);
    }

    public static Router get() {
        return instance;
    }

    public Stage stage() {
        return stage;
    }

    /** Mostra a tela de login em tela cheia (sem AppShell). */
    public void showLogin() {
        shell = null;
        contentLayer.getChildren().setAll(load("login").root());
    }

    /** Carrega o AppShell e abre a tela inicial dentro dele. */
    public void showShell(String initialView) {
        Loaded loaded = load("app-shell");
        shell = (AppShellController) loaded.controller();
        contentLayer.getChildren().setAll(loaded.root());
        navigate(initialView);
    }

    /** Troca a tela exibida na área central do AppShell. */
    public void navigate(String viewName) {
        if (shell == null) {
            showShell(viewName);
            return;
        }
        Loaded loaded = load(viewName);
        shell.setContent(viewName, loaded.root());
    }

    private Loaded load(String viewName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/henrique/ifconecta/desktop/view/" + viewName + ".fxml"));
            Parent root = loader.load();
            return new Loaded(root, loader.getController());
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao carregar a tela '" + viewName + "': " + e.getMessage(), e);
        }
    }

    private record Loaded(Parent root, Object controller) {
    }
}
