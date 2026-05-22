package com.henrique.ifconecta.desktop.ui;

import java.util.function.Consumer;

import com.henrique.ifconecta.desktop.core.Router;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/** Abre uma janela modal a partir de um FXML, aplicando o tema atual. */
public final class Modal {

    private Modal() {
    }

    /**
     * Carrega o FXML como janela modal, deixa o chamador configurar o controller
     * e exibe a janela bloqueando até ela ser fechada.
     *
     * @param fxmlName   nome do arquivo em view/ (sem extensão)
     * @param titulo     título da janela
     * @param configurar recebe o controller antes da janela abrir
     */
    public static <C> void abrir(String fxmlName, String titulo, Consumer<C> configurar) {
        try {
            FXMLLoader loader = new FXMLLoader(Modal.class.getResource(
                    "/com/henrique/ifconecta/desktop/view/" + fxmlName + ".fxml"));
            Parent root = loader.load();

            Stage janela = new Stage();
            janela.initModality(Modality.APPLICATION_MODAL);
            janela.initOwner(Router.get().stage());
            janela.setTitle(titulo);
            janela.setResizable(true);
            janela.setMinWidth(360);
            janela.setMinHeight(280);

            Scene scene = new Scene(root);
            Theme.applyTo(scene);
            janela.setScene(scene);

            configurar.accept(loader.getController());

            janela.showAndWait();
        } catch (Exception e) {
            Toast.error("Não foi possível abrir a janela", e.getMessage());
        }
    }

    /** Fecha a janela modal a que o nó pertence. */
    public static void fechar(javafx.scene.Node noDaJanela) {
        if (noDaJanela.getScene() != null && noDaJanela.getScene().getWindow() instanceof Stage stage) {
            stage.close();
        }
    }
}
