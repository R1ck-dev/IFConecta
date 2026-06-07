package com.henrique.ifconecta.desktop.controller;

import com.henrique.ifconecta.desktop.core.AsyncRunner;
import com.henrique.ifconecta.desktop.core.http.ApiException;
import com.henrique.ifconecta.desktop.service.AdminService;
import com.henrique.ifconecta.desktop.ui.Toast;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Painel administrativo — convite de professores e institucionais
 * (endpoints /api/admin/*, apenas ADMIN).
 */
public class AdminController {

    private static final String EMAIL_REGEX = "^\\S+@\\S+\\.\\S+$";

    // ── Convidar professor ──
    @FXML private TextField profNomeField;
    @FXML private TextField profEmailField;
    @FXML private TextField profSiapeField;
    @FXML private Label profError;
    @FXML private Button profBtn;

    // ── Convidar institucional ──
    @FXML private TextField instNomeField;
    @FXML private TextField instEmailField;
    @FXML private TextField instSetorField;
    @FXML private TextField instCargoField;
    @FXML private Label instError;
    @FXML private Button instBtn;

    // ───────── Convidar professor ─────────

    @FXML
    private void onConvidarProfessor() {
        String nome = valor(profNomeField);
        String email = valor(profEmailField);
        String siape = valor(profSiapeField);

        if (nome.isEmpty() || email.isEmpty() || siape.isEmpty()) {
            marcarErro(profError, "Preencha nome, e-mail e SIAPE.");
            return;
        }
        if (!email.matches(EMAIL_REGEX)) {
            marcarErro(profError, "E-mail em formato inválido.");
            return;
        }
        limparErro(profError);

        profBtn.setDisable(true);
        AsyncRunner.runVoid(
                () -> AdminService.convidarProfessor(nome, email, siape),
                () -> {
                    profBtn.setDisable(false);
                    Toast.success("Convite enviado por e-mail.");
                    profNomeField.clear();
                    profEmailField.clear();
                    profSiapeField.clear();
                },
                erro -> {
                    profBtn.setDisable(false);
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível enviar o convite", mensagem(erro));
                    }
                });
    }

    // ───────── Convidar institucional ─────────

    @FXML
    private void onConvidarInstitucional() {
        String nome = valor(instNomeField);
        String email = valor(instEmailField);
        String setor = valor(instSetorField);
        String cargo = valor(instCargoField);

        if (nome.isEmpty() || email.isEmpty() || setor.isEmpty() || cargo.isEmpty()) {
            marcarErro(instError, "Preencha nome, e-mail, setor e cargo.");
            return;
        }
        if (!email.matches(EMAIL_REGEX)) {
            marcarErro(instError, "E-mail em formato inválido.");
            return;
        }
        limparErro(instError);

        instBtn.setDisable(true);
        AsyncRunner.runVoid(
                () -> AdminService.convidarInstitucional(nome, email, setor, cargo),
                () -> {
                    instBtn.setDisable(false);
                    Toast.success("Convite enviado por e-mail.");
                    instNomeField.clear();
                    instEmailField.clear();
                    instSetorField.clear();
                    instCargoField.clear();
                },
                erro -> {
                    instBtn.setDisable(false);
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível enviar o convite", mensagem(erro));
                    }
                });
    }

    // ───────── Auxiliares ─────────

    private static void marcarErro(Label label, String msg) {
        label.setText(msg);
        label.setVisible(true);
        label.setManaged(true);
    }

    private static void limparErro(Label label) {
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
