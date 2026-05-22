package com.henrique.ifconecta.desktop.controller;

import com.henrique.ifconecta.desktop.core.AsyncRunner;
import com.henrique.ifconecta.desktop.core.http.ApiException;
import com.henrique.ifconecta.desktop.service.ClubeService;
import com.henrique.ifconecta.desktop.ui.Modal;
import com.henrique.ifconecta.desktop.ui.Toast;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

/** Modal de criação de clube. */
public class CriarClubeController {

    @FXML private TextField nomeField;
    @FXML private TextArea descricaoArea;
    @FXML private ComboBox<String> tipoCombo;
    @FXML private Label erroLabel;
    @FXML private Button cancelarBtn;
    @FXML private Button criarBtn;

    private Runnable aoCriar;

    @FXML
    private void initialize() {
        tipoCombo.getItems().setAll("PUBLICO", "PRIVADO");
        tipoCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(String valor) {
                return "PRIVADO".equals(valor) ? "Privado" : "Público";
            }

            @Override
            public String fromString(String texto) {
                return "Privado".equals(texto) ? "PRIVADO" : "PUBLICO";
            }
        });
        tipoCombo.getSelectionModel().select("PUBLICO");
    }

    /** Guarda o callback executado após a criação bem-sucedida. */
    public void configurar(Runnable aoCriar) {
        this.aoCriar = aoCriar;
    }

    @FXML
    private void onCancelar() {
        Modal.fechar(cancelarBtn);
    }

    @FXML
    private void onCriar() {
        String nome = nomeField.getText() == null ? "" : nomeField.getText().trim();
        String descricao = descricaoArea.getText() == null ? "" : descricaoArea.getText().trim();
        String tipoAcesso = tipoCombo.getValue();

        if (nome.isEmpty()) {
            mostrarErro("Informe o nome do clube.");
            return;
        }
        if (descricao.isEmpty()) {
            mostrarErro("Informe uma descrição para o clube.");
            return;
        }
        if (tipoAcesso == null) {
            mostrarErro("Selecione o tipo de acesso.");
            return;
        }
        esconderErro();
        criarBtn.setDisable(true);
        cancelarBtn.setDisable(true);

        AsyncRunner.runVoid(
                () -> ClubeService.criar(nome, descricao, tipoAcesso),
                () -> {
                    if (aoCriar != null) {
                        aoCriar.run();
                    }
                    Toast.success("Clube criado!");
                    Modal.fechar(criarBtn);
                },
                erro -> {
                    criarBtn.setDisable(false);
                    cancelarBtn.setDisable(false);
                    mostrarErro(mensagem(erro));
                });
    }

    private void mostrarErro(String texto) {
        erroLabel.setText(texto);
        erroLabel.setVisible(true);
        erroLabel.setManaged(true);
    }

    private void esconderErro() {
        erroLabel.setVisible(false);
        erroLabel.setManaged(false);
    }

    private static String mensagem(Throwable erro) {
        if (erro instanceof ApiException api) {
            return api.getMessage();
        }
        return "Tente novamente em instantes.";
    }
}
