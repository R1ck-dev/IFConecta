package com.henrique.ifconecta.desktop.controller;

import java.util.List;

import com.henrique.ifconecta.desktop.core.AsyncRunner;
import com.henrique.ifconecta.desktop.core.Router;
import com.henrique.ifconecta.desktop.core.http.ApiException;
import com.henrique.ifconecta.desktop.model.Curso;
import com.henrique.ifconecta.desktop.service.CursoService;
import com.henrique.ifconecta.desktop.service.UsuarioService;
import com.henrique.ifconecta.desktop.ui.Toast;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

/**
 * Tela de cadastro de aluno — tela cheia, pré-login.
 * Faz POST /api/usuarios/alunos e volta ao login após o sucesso.
 */
public class CadastroController {

    /** Formato do prontuário: SL + 6 dígitos + 1 letra. */
    private static final String PRONTUARIO_REGEX = "^[Ss][Ll]\\d{6}[A-Za-z]$";

    @FXML private HBox nomeGroup;
    @FXML private HBox emailGroup;
    @FXML private HBox senhaGroup;
    @FXML private HBox prontuarioGroup;
    @FXML private TextField nomeField;
    @FXML private TextField emailField;
    @FXML private PasswordField senhaField;
    @FXML private TextField prontuarioField;
    @FXML private ComboBox<Curso> cursoCombo;
    @FXML private Label nomeError;
    @FXML private Label emailError;
    @FXML private Label senhaError;
    @FXML private Label prontuarioError;
    @FXML private Label cursoError;
    @FXML private Button cadastrarBtn;

    @FXML
    private void initialize() {
        cursoCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Curso curso) {
                return curso == null ? "" : curso.nome();
            }

            @Override
            public Curso fromString(String texto) {
                return null;
            }
        });

        AsyncRunner.run(
                CursoService::listar,
                cursos -> cursoCombo.getItems().setAll(cursos == null ? List.of() : cursos),
                erro -> Toast.error("Não foi possível carregar os cursos", mensagem(erro)));
    }

    @FXML
    private void onVoltarLogin() {
        Router.get().showLogin();
    }

    @FXML
    private void onCadastrar() {
        String nome = valor(nomeField);
        String email = valor(emailField);
        String senha = senhaField.getText() == null ? "" : senhaField.getText();
        String prontuario = valor(prontuarioField);
        Curso curso = cursoCombo.getValue();

        boolean valido = true;
        if (nome.isEmpty()) {
            valido = marcarErro(nomeGroup, nomeError, "Informe seu nome.");
        } else {
            limparErro(nomeGroup, nomeError);
        }
        if (email.isEmpty()) {
            valido = marcarErro(emailGroup, emailError, "Informe seu e-mail.") && valido;
        } else if (!email.matches("^\\S+@\\S+\\.\\S+$")) {
            valido = marcarErro(emailGroup, emailError, "E-mail em formato inválido.") && valido;
        } else {
            limparErro(emailGroup, emailError);
        }
        if (senha.isEmpty()) {
            valido = marcarErro(senhaGroup, senhaError, "Informe uma senha.") && valido;
        } else if (senha.length() < 8) {
            valido = marcarErro(senhaGroup, senhaError, "A senha deve ter no mínimo 8 caracteres.") && valido;
        } else {
            limparErro(senhaGroup, senhaError);
        }
        if (prontuario.isEmpty()) {
            valido = marcarErro(prontuarioGroup, prontuarioError, "Informe seu prontuário.") && valido;
        } else if (!prontuario.matches(PRONTUARIO_REGEX)) {
            valido = marcarErro(prontuarioGroup, prontuarioError,
                    "Use o formato SL000000X (SL + 6 dígitos + 1 letra).") && valido;
        } else {
            limparErro(prontuarioGroup, prontuarioError);
        }
        if (curso == null) {
            valido = marcarErro(cursoError, "Selecione seu curso.") && valido;
        } else {
            limparErro(cursoError);
        }
        if (!valido) {
            return;
        }

        setLoading(true);
        AsyncRunner.runVoid(
                () -> UsuarioService.registrarAluno(curso.id(), nome, email, senha, prontuario),
                () -> {
                    setLoading(false);
                    Toast.success("Conta criada!",
                            "Enviamos um e-mail de ativação. Verifique sua caixa de entrada.");
                    Router.get().showLogin();
                },
                erro -> {
                    setLoading(false);
                    Toast.error("Não foi possível criar a conta", mensagem(erro));
                });
    }

    private void setLoading(boolean loading) {
        cadastrarBtn.setDisable(loading);
        cadastrarBtn.setText(loading ? "Cadastrando…" : "Cadastrar");
        nomeField.setDisable(loading);
        emailField.setDisable(loading);
        senhaField.setDisable(loading);
        prontuarioField.setDisable(loading);
        cursoCombo.setDisable(loading);
    }

    private boolean marcarErro(HBox grupo, Label label, String msg) {
        if (!grupo.getStyleClass().contains("has-error")) {
            grupo.getStyleClass().add("has-error");
        }
        return marcarErro(label, msg);
    }

    private boolean marcarErro(Label label, String msg) {
        label.setText(msg);
        label.setVisible(true);
        label.setManaged(true);
        return false;
    }

    private void limparErro(HBox grupo, Label label) {
        grupo.getStyleClass().remove("has-error");
        limparErro(label);
    }

    private void limparErro(Label label) {
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
