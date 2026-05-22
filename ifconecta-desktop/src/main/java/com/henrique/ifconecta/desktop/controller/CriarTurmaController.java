package com.henrique.ifconecta.desktop.controller;

import com.henrique.ifconecta.desktop.core.AsyncRunner;
import com.henrique.ifconecta.desktop.core.http.ApiException;
import com.henrique.ifconecta.desktop.model.DisciplinaResumo;
import com.henrique.ifconecta.desktop.model.UsuarioPublico;
import com.henrique.ifconecta.desktop.service.CursoService;
import com.henrique.ifconecta.desktop.service.UsuarioService;
import com.henrique.ifconecta.desktop.ui.Modal;
import com.henrique.ifconecta.desktop.ui.Toast;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

/**
 * Modal de solicitação de turma — port do modal de academico.jsx do front web.
 * Carrega as combos de disciplina e professor e dispara POST /api/turmas.
 */
public class CriarTurmaController {

    @FXML private ComboBox<DisciplinaResumo> disciplinaCombo;
    @FXML private ComboBox<UsuarioPublico> professorCombo;
    @FXML private TextField semestreField;
    @FXML private TextField codigoField;
    @FXML private Label erroLabel;
    @FXML private Button confirmarBtn;

    private Runnable aoCriar;

    @FXML
    private void initialize() {
        disciplinaCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(DisciplinaResumo d) {
                return d == null ? "" : d.nome() + " — " + d.cursoSigla();
            }

            @Override
            public DisciplinaResumo fromString(String texto) {
                return null;
            }
        });
        professorCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(UsuarioPublico u) {
                return u == null ? "" : u.nome();
            }

            @Override
            public UsuarioPublico fromString(String texto) {
                return null;
            }
        });

        AsyncRunner.run(
                CursoService::disciplinas,
                disciplinas -> disciplinaCombo.getItems().setAll(disciplinas),
                erro -> Toast.error("Não foi possível carregar as disciplinas", mensagem(erro)));

        AsyncRunner.run(
                UsuarioService::professores,
                professores -> professorCombo.getItems().setAll(professores),
                erro -> Toast.error("Não foi possível carregar os professores", mensagem(erro)));
    }

    /** Define o callback executado após a turma ser criada com sucesso. */
    public void configurar(Runnable aoCriar) {
        this.aoCriar = aoCriar;
    }

    @FXML
    private void onCancelar() {
        Modal.fechar(confirmarBtn);
    }

    @FXML
    private void onConfirmar() {
        DisciplinaResumo disciplina = disciplinaCombo.getValue();
        UsuarioPublico professor = professorCombo.getValue();
        String semestre = valor(semestreField);
        String codigo = valor(codigoField);

        if (disciplina == null) {
            erro("Selecione uma disciplina.");
            return;
        }
        if (professor == null) {
            erro("Selecione um professor.");
            return;
        }
        if (semestre.isEmpty()) {
            erro("Informe o semestre.");
            return;
        }
        if (codigo.isEmpty()) {
            erro("Informe o código da turma.");
            return;
        }
        limparErro();

        confirmarBtn.setDisable(true);
        confirmarBtn.setText("Solicitando…");
        AsyncRunner.runVoid(
                () -> com.henrique.ifconecta.desktop.service.TurmaService.criar(
                        disciplina.id(), professor.id(), semestre, codigo),
                () -> {
                    if (aoCriar != null) {
                        aoCriar.run();
                    }
                    Toast.success("Turma solicitada!", "Aguarde a aprovação do professor.");
                    Modal.fechar(confirmarBtn);
                },
                erro -> {
                    confirmarBtn.setDisable(false);
                    confirmarBtn.setText("Solicitar turma");
                    erro("Não foi possível solicitar a turma. " + mensagem(erro));
                });
    }

    private void erro(String msg) {
        erroLabel.setText(msg);
        erroLabel.setVisible(true);
        erroLabel.setManaged(true);
    }

    private void limparErro() {
        erroLabel.setVisible(false);
        erroLabel.setManaged(false);
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
