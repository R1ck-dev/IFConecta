package com.henrique.ifconecta.desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Classe principal do programa. Abre a janela e controla a troca de telas.
 *
 * Como funciona a navegacao:
 *  - Antes do login, cada tela ocupa a janela inteira (Login, Cadastro).
 *  - Depois do login, existe uma "moldura" (AppShell) com cabecalho e menu;
 *    so o meio dela e trocado quando o usuario clica em algo no menu.
 */
public class App extends Application {

    /** A janela do programa. */
    private static Stage janela;

    /** A moldura da area logada (cabecalho + menu). O centro dela e que troca. */
    private static BorderPane areaLogada;

    /** Recado de uma tela para a proxima (ex.: o id de um clube ou post). */
    public static String parametro;

    @Override
    public void start(Stage stage) {
        janela = stage;
        janela.setTitle("IFConecta — Campus Salto");
        janela.setMinWidth(960);
        janela.setMinHeight(640);
        abrirTelaCheia("Login");
        janela.show();
    }

    /** Mostra uma tela que ocupa a janela inteira (Login e Cadastro). */
    public static void abrirTelaCheia(String nomeFxml) {
        try {
            Parent raiz = FXMLLoader.load(App.class.getResource("view/" + nomeFxml + ".fxml"));
            janela.setScene(novaCena(raiz));
        } catch (Exception e) {
            erro(e);
        }
    }

    /** Depois do login: abre a moldura e ja mostra a Timeline no meio. */
    public static void abrirAreaLogada() {
        try {
            areaLogada = FXMLLoader.load(App.class.getResource("view/AppShell.fxml"));
            janela.setScene(novaCena(areaLogada));
            mostrarConteudo("Timeline");
        } catch (Exception e) {
            erro(e);
        }
    }

    /** Troca apenas a tela do meio da area logada. */
    public static void mostrarConteudo(String nomeFxml) {
        try {
            Parent tela = FXMLLoader.load(App.class.getResource("view/" + nomeFxml + ".fxml"));
            areaLogada.setCenter(tela);
        } catch (Exception e) {
            erro(e);
        }
    }

    /** Cria uma cena ja com a folha de estilo aplicada. */
    private static Scene novaCena(Parent raiz) {
        Scene cena = new Scene(raiz, 1180, 760);
        cena.getStylesheets().add(App.class.getResource("estilo.css").toExternalForm());
        return cena;
    }

    /** Mostra uma mensagem simples para o usuario. */
    public static void avisar(String mensagem) {
        new Alert(Alert.AlertType.INFORMATION, mensagem).showAndWait();
    }

    /** Mostra uma mensagem de erro. */
    public static void erro(Throwable problema) {
        String texto = problema.getMessage();
        if (texto == null || texto.isBlank()) {
            texto = "Algo deu errado. Tente novamente.";
        }
        new Alert(Alert.AlertType.ERROR, texto).showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
