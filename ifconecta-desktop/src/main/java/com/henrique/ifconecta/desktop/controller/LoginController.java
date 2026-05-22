package com.henrique.ifconecta.desktop.controller;

import org.kordamp.ikonli.javafx.FontIcon;

import com.henrique.ifconecta.desktop.core.AsyncRunner;
import com.henrique.ifconecta.desktop.core.Router;
import com.henrique.ifconecta.desktop.core.Session;
import com.henrique.ifconecta.desktop.core.http.ApiException;
import com.henrique.ifconecta.desktop.model.MeuPerfil;
import com.henrique.ifconecta.desktop.model.TokenResponse;
import com.henrique.ifconecta.desktop.service.AuthService;
import com.henrique.ifconecta.desktop.ui.Toast;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * Tela de login — port de LoginPage (ifconecta-web/src/pages/auth.jsx).
 * Faz POST /auth/login, busca o perfil e abre o AppShell na timeline.
 */
public class LoginController {

    @FXML private HBox emailGroup;
    @FXML private HBox senhaGroup;
    @FXML private TextField emailField;
    @FXML private PasswordField senhaField;
    @FXML private TextField senhaVisible;
    @FXML private FontIcon toggleSenhaIcon;
    @FXML private Label emailError;
    @FXML private Label senhaError;
    @FXML private Button entrarBtn;

    @FXML
    private void initialize() {
        // Campo "mostrar senha": espelha o conteúdo do PasswordField.
        senhaVisible.textProperty().bindBidirectional(senhaField.textProperty());

        // Enter em qualquer campo envia o formulário.
        emailField.setOnAction(e -> onEntrar());
        senhaField.setOnAction(e -> onEntrar());
        senhaVisible.setOnAction(e -> onEntrar());
    }

    @FXML
    private void onToggleSenha() {
        boolean mostrando = senhaVisible.isVisible();
        senhaVisible.setVisible(!mostrando);
        senhaVisible.setManaged(!mostrando);
        senhaField.setVisible(mostrando);
        senhaField.setManaged(mostrando);
        toggleSenhaIcon.setIconLiteral(mostrando ? "fth-eye" : "fth-eye-off");
        (mostrando ? senhaField : senhaVisible).requestFocus();
    }

    @FXML
    private void onEsqueci() {
        Router.get().showFullScreen("esqueci-senha");
    }

    @FXML
    private void onCadastrar() {
        Router.get().showFullScreen("cadastro");
    }

    @FXML
    private void onEntrar() {
        String email = valor(emailField);
        String senha = senhaField.getText() == null ? "" : senhaField.getText();

        boolean valido = true;
        if (email.isEmpty()) {
            valido = marcarErro(emailGroup, emailError, "Informe seu email.");
        } else if (!email.matches("^\\S+@\\S+\\.\\S+$")) {
            valido = marcarErro(emailGroup, emailError, "Email em formato inválido.");
        } else {
            limparErro(emailGroup, emailError);
        }
        if (senha.isEmpty()) {
            valido = marcarErro(senhaGroup, senhaError, "Informe sua senha.") && valido;
        } else {
            limparErro(senhaGroup, senhaError);
        }
        if (!valido) {
            return;
        }

        setLoading(true);
        AsyncRunner.run(
                () -> {
                    TokenResponse token = AuthService.login(email, senha);
                    Session.get().set(token.token(), null);
                    MeuPerfil me = AuthService.getMe();
                    Session.get().set(token.token(), me);
                    return me;
                },
                me -> {
                    setLoading(false);
                    Toast.success("Bem-vindo(a) de volta!");
                    Router.get().showShell("timeline");
                },
                erro -> {
                    setLoading(false);
                    Session.get().clear();
                    Toast.error("Falha ao entrar", mensagem(erro));
                });
    }

    private void setLoading(boolean loading) {
        entrarBtn.setDisable(loading);
        entrarBtn.setText(loading ? "Entrando…" : "Entrar");
        emailField.setDisable(loading);
        senhaField.setDisable(loading);
        senhaVisible.setDisable(loading);
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
            return api.isUnauthorized() ? "Email ou senha inválidos." : api.getMessage();
        }
        return "Email ou senha inválidos.";
    }
}
