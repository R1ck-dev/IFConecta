package com.henrique.ifconecta.desktop.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.henrique.ifconecta.desktop.core.AsyncRunner;
import com.henrique.ifconecta.desktop.core.http.ApiException;
import com.henrique.ifconecta.desktop.service.AdminService;
import com.henrique.ifconecta.desktop.ui.Icons;
import com.henrique.ifconecta.desktop.ui.Toast;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * Painel administrativo — convite de professores/institucionais e cadastro
 * de cursos com disciplinas (endpoints /api/admin/*, apenas ADMIN).
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

    // ── Cadastrar curso ──
    @FXML private TextField cursoNomeField;
    @FXML private TextField cursoSiglaField;
    @FXML private ComboBox<String> modalidadeCombo;
    @FXML private VBox disciplinasBox;
    @FXML private Button addDisciplinaBtn;
    @FXML private Label cursoError;
    @FXML private Button cursoBtn;

    @FXML
    private void initialize() {
        modalidadeCombo.getItems().setAll("INTEGRADO", "SUBSEQUENTE", "SUPERIOR", "POS_GRADUACAO");
        modalidadeCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(String valor) {
                return rotuloModalidade(valor);
            }

            @Override
            public String fromString(String texto) {
                return texto;
            }
        });
        adicionarLinhaDisciplina();
    }

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

    // ───────── Cadastrar curso ─────────

    @FXML
    private void onAdicionarDisciplina() {
        adicionarLinhaDisciplina();
    }

    private void adicionarLinhaDisciplina() {
        HBox linha = new HBox(8);
        linha.setAlignment(Pos.CENTER_LEFT);
        linha.getStyleClass().add("disciplina-linha");

        TextField nomeField = new TextField();
        nomeField.getStyleClass().add("input");
        nomeField.setPromptText("Nome da disciplina");
        HBox.setHgrow(nomeField, Priority.ALWAYS);

        TextField cargaField = new TextField();
        cargaField.getStyleClass().add("input");
        cargaField.setPromptText("Carga horária");
        cargaField.setPrefWidth(140);

        Button remover = new Button();
        remover.getStyleClass().addAll("btn", "btn-ghost", "btn-sm");
        remover.setGraphic(Icons.of("fth-x", 14));
        remover.setOnAction(e -> {
            disciplinasBox.getChildren().remove(linha);
            if (disciplinasBox.getChildren().isEmpty()) {
                adicionarLinhaDisciplina();
            }
        });

        linha.getChildren().addAll(nomeField, cargaField, remover);
        disciplinasBox.getChildren().add(linha);
    }

    @FXML
    private void onCadastrarCurso() {
        String nome = valor(cursoNomeField);
        String sigla = valor(cursoSiglaField);
        String modalidade = modalidadeCombo.getValue();

        if (nome.isEmpty() || sigla.isEmpty()) {
            marcarErro(cursoError, "Preencha o nome e a sigla do curso.");
            return;
        }
        if (modalidade == null || modalidade.isBlank()) {
            marcarErro(cursoError, "Selecione a modalidade do curso.");
            return;
        }

        List<Map<String, Object>> disciplinas = new ArrayList<>();
        for (javafx.scene.Node node : disciplinasBox.getChildren()) {
            if (!(node instanceof HBox linha) || linha.getChildren().size() < 2) {
                continue;
            }
            TextField nomeDiscField = (TextField) linha.getChildren().get(0);
            TextField cargaDiscField = (TextField) linha.getChildren().get(1);

            String nomeDisc = nomeDiscField.getText() == null
                    ? "" : nomeDiscField.getText().trim();
            String cargaStr = cargaDiscField.getText() == null
                    ? "" : cargaDiscField.getText().trim();

            if (nomeDisc.isEmpty() && cargaStr.isEmpty()) {
                continue;
            }
            if (nomeDisc.isEmpty()) {
                marcarErro(cursoError, "Informe o nome de todas as disciplinas.");
                return;
            }
            int cargaHoraria;
            try {
                cargaHoraria = Integer.parseInt(cargaStr);
            } catch (NumberFormatException ex) {
                marcarErro(cursoError,
                        "Carga horária de \"" + nomeDisc + "\" deve ser um número.");
                return;
            }
            if (cargaHoraria <= 0) {
                marcarErro(cursoError,
                        "Carga horária de \"" + nomeDisc + "\" deve ser maior que zero.");
                return;
            }
            Map<String, Object> disciplina = new LinkedHashMap<>();
            disciplina.put("nome", nomeDisc);
            disciplina.put("cargaHoraria", cargaHoraria);
            disciplinas.add(disciplina);
        }

        if (disciplinas.isEmpty()) {
            marcarErro(cursoError, "Adicione ao menos uma disciplina.");
            return;
        }
        limparErro(cursoError);

        cursoBtn.setDisable(true);
        AsyncRunner.runVoid(
                () -> AdminService.criarCursoComDisciplinas(nome, sigla, modalidade, disciplinas),
                () -> {
                    cursoBtn.setDisable(false);
                    Toast.success("Curso cadastrado com sucesso!");
                    cursoNomeField.clear();
                    cursoSiglaField.clear();
                    modalidadeCombo.setValue(null);
                    disciplinasBox.getChildren().clear();
                    adicionarLinhaDisciplina();
                },
                erro -> {
                    cursoBtn.setDisable(false);
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível cadastrar o curso", mensagem(erro));
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

    private static String rotuloModalidade(String valor) {
        if (valor == null) {
            return "";
        }
        return switch (valor) {
            case "INTEGRADO" -> "Integrado";
            case "SUBSEQUENTE" -> "Subsequente";
            case "SUPERIOR" -> "Superior";
            case "POS_GRADUACAO" -> "Pós-graduação";
            default -> valor;
        };
    }
}
