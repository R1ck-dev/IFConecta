package com.henrique.ifconecta.desktop;

import com.henrique.ifconecta.desktop.core.Router;
import com.henrique.ifconecta.desktop.core.Session;
import com.henrique.ifconecta.desktop.core.http.ApiClient;
import com.henrique.ifconecta.desktop.ui.Theme;
import com.henrique.ifconecta.desktop.ui.Toast;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Ponto de entrada do cliente desktop IFConecta.
 *
 * <p>Monta a cena com duas camadas empilhadas: a camada de conteúdo (telas)
 * e a camada de toasts. Instala tema, roteador e o handler de sessão expirada,
 * e abre a tela de login.</p>
 */
public class IFConectaApp extends Application {

    @Override
    public void start(Stage stage) {
        StackPane contentLayer = new StackPane();

        VBox toastLayer = new VBox(10);
        toastLayer.setAlignment(Pos.BOTTOM_RIGHT);
        toastLayer.setPadding(new Insets(20));
        toastLayer.setMouseTransparent(true);

        StackPane rootStack = new StackPane(contentLayer, toastLayer);
        StackPane.setAlignment(toastLayer, Pos.BOTTOM_RIGHT);

        Scene scene = new Scene(rootStack, 1180, 760);
        Theme.install(scene);
        Toast.install(toastLayer);
        Router.init(stage, contentLayer);

        // Sessão expirada (401) → limpa estado e volta ao login.
        ApiClient.setUnauthorizedHandler(() -> {
            Session.get().clear();
            Router.get().showLogin();
            Toast.error("Sessão expirada", "Entre novamente para continuar.");
        });

        Router.get().showLogin();

        stage.setScene(scene);
        stage.setTitle("IFConecta — Campus Salto");
        stage.setMinWidth(960);
        stage.setMinHeight(640);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
