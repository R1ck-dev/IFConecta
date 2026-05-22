package com.henrique.ifconecta.desktop.controller;

import com.henrique.ifconecta.desktop.core.AsyncRunner;
import com.henrique.ifconecta.desktop.core.Router;
import com.henrique.ifconecta.desktop.core.http.ApiException;
import com.henrique.ifconecta.desktop.service.UsuarioService;
import com.henrique.ifconecta.desktop.ui.Toast;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * Tela de recuperação de senha — tela cheia, pré-login.
 * Faz POST /api/usuarios/esqueci-senha e volta ao login após o sucesso.
 */
public class EsqueciSenhaController {

    @FXML private HBox emailGroup;
    @FXML private TextField emailField;
    @FXML private Label emailError;
    @FXML private Button enviarBtn;

    @FXML
    private void initialize() {
        emailField.setOnAction(e -> onEnviar());
    }

    @FXML
    private void onVoltarLogin() {
        Router.get().showLogin();
    }

    @FXML
    private void onEnviar() {
        String email = valor(emailField);

        boolean valido;
        if (email.isEmpty()) {
            valido = marcarErro(emailGroup, emailError, "Informe seu e-mail.");
        } else if (!email.matches("^\\S+@\\S+\\.\\S+$")) {
            valido = marcarErro(emailGroup, emailError, "E-mail em formato inválido.");
        } else {
            limparErro(emailGroup, emailError);
            valido = true;
        }
        if (!valido) {
            return;
        }

        setLoading(true);
        AsyncRunner.runVoid(
                () -> UsuarioService.esqueciSenha(email),
                () -> {
                    setLoading(false);
                    Toast.success("Pronto!",
                            "Se houver uma conta com este e-mail, o link de redefinição foi enviado.");
                    Router.get().showLogin();
                },
                erro -> {
                    setLoading(false);
                    Toast.error("Não foi possível enviar o link", mensagem(erro));
                });
    }

    private void setLoading(boolean loading) {
        enviarBtn.setDisable(loading);
        enviarBtn.setText(loading ? "Enviando…" : "Enviar link");
        emailField.setDisable(loading);
    }

    private boolean marcarErro(HBox grupo, Label label, String msg) {
        if (!grupo.getStyleClass().contains("has-error")) {
            grupo.getStyleClass().add("has-error");
        }
        label.setText(msg);
        label.setVisible(true);
        label.setManaged(true);
        return false;
    }

    private void limparErro(HBox grupo, Label label) {
        grupo.getStyleClass().remove("has-error");
        label.setVisible(false);
        label.setManaged(false);
    }

    private static String valor(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }

    private static String mensagem(Throwable erro) {
        if (erro instanceof ApiException api) {
            return api.getMessage();
        }
        return "Tente novamente em instantes.";
    }
}
