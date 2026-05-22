package com.henrique.ifconecta.desktop.controller;

import com.henrique.ifconecta.desktop.core.AsyncRunner;
import com.henrique.ifconecta.desktop.core.http.ApiException;
import com.henrique.ifconecta.desktop.model.ClubeResumo;
import com.henrique.ifconecta.desktop.model.Curso;
import com.henrique.ifconecta.desktop.model.TurmaResumo;
import com.henrique.ifconecta.desktop.service.ClubeService;
import com.henrique.ifconecta.desktop.service.CursoService;
import com.henrique.ifconecta.desktop.service.NotificacaoService;
import com.henrique.ifconecta.desktop.service.TurmaService;
import com.henrique.ifconecta.desktop.ui.Modal;
import com.henrique.ifconecta.desktop.ui.Toast;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * Modal de envio de comunicado — escolhe o público alvo (geral, curso, turma
 * ou clube) e dispara uma notificação para os destinatários correspondentes.
 */
public class ComunicadoController {

    private static final String GERAL = "GERAL";
    private static final String CURSO = "CURSO";
    private static final String TURMA = "TURMA";
    private static final String CLUBE = "CLUBE";

    @FXML private VBox raiz;
    @FXML private TextField tituloField;
    @FXML private Label tituloErro;
    @FXML private TextArea mensagemArea;
    @FXML private Label mensagemErro;
    @FXML private ComboBox<String> tipoAlvoCombo;
    @FXML private VBox cursoBox;
    @FXML private ComboBox<Curso> cursoCombo;
    @FXML private VBox turmaBox;
    @FXML private ComboBox<TurmaResumo> turmaCombo;
    @FXML private VBox clubeBox;
    @FXML private ComboBox<ClubeResumo> clubeCombo;
    @FXML private Label alvoErro;
    @FXML private Button enviarBtn;

    private boolean cursosCarregados;
    private boolean turmasCarregadas;
    private boolean clubesCarregados;

    /** Quando preenchido, o modal fica travado em CLUBE neste clube. */
    private String clubeFixadoId;

    @FXML
    private void initialize() {
        tipoAlvoCombo.getItems().setAll(GERAL, CURSO, TURMA, CLUBE);

        cursoCombo.setConverter(conversor(c -> c == null ? null
                : c.nome() + (c.sigla() == null || c.sigla().isBlank() ? "" : " (" + c.sigla() + ")")));
        turmaCombo.setConverter(conversor(t -> t == null ? null : descreverTurma(t)));
        clubeCombo.setConverter(conversor(c -> c == null ? null : c.nome()));

        tipoAlvoCombo.valueProperty().addListener((obs, antigo, novo) -> aoMudarTipoAlvo(novo));
        tipoAlvoCombo.setValue(GERAL);
    }

    /**
     * Pré-configura o modal para enviar um comunicado a um clube específico,
     * travando o público em CLUBE e o destino no clube informado. Usado pelo
     * líder do clube a partir da tela de detalhe.
     */
    public void fixarClube(String clubeId) {
        this.clubeFixadoId = clubeId;
        tipoAlvoCombo.setValue(CLUBE);
        tipoAlvoCombo.setDisable(true);
        clubeCombo.setDisable(true);
    }

    // ───────── Tipo de alvo ─────────

    private void aoMudarTipoAlvo(String tipo) {
        String alvo = tipo == null ? GERAL : tipo;
        mostrar(cursoBox, CURSO.equals(alvo));
        mostrar(turmaBox, TURMA.equals(alvo));
        mostrar(clubeBox, CLUBE.equals(alvo));
        limparErroAlvo();

        switch (alvo) {
            case CURSO -> carregarCursos();
            case TURMA -> carregarTurmas();
            case CLUBE -> carregarClubes();
            default -> { /* GERAL não tem campo de alvo */ }
        }
    }

    private void carregarCursos() {
        if (cursosCarregados) {
            return;
        }
        cursosCarregados = true;
        AsyncRunner.run(
                CursoService::listar,
                cursos -> cursoCombo.getItems().setAll(cursos),
                erro -> {
                    cursosCarregados = false;
                    erroCarga("os cursos", erro);
                });
    }

    private void carregarTurmas() {
        if (turmasCarregadas) {
            return;
        }
        turmasCarregadas = true;
        AsyncRunner.run(
                TurmaService::minhas,
                turmas -> turmaCombo.getItems().setAll(turmas),
                erro -> {
                    turmasCarregadas = false;
                    erroCarga("as turmas", erro);
                });
    }

    private void carregarClubes() {
        if (clubesCarregados) {
            return;
        }
        clubesCarregados = true;
        AsyncRunner.run(
                ClubeService::meusClubes,
                clubes -> {
                    clubeCombo.getItems().setAll(clubes);
                    if (clubeFixadoId != null) {
                        clubes.stream()
                                .filter(c -> clubeFixadoId.equals(c.id()))
                                .findFirst()
                                .ifPresent(clubeCombo::setValue);
                    }
                },
                erro -> {
                    clubesCarregados = false;
                    erroCarga("os clubes", erro);
                });
    }

    private void erroCarga(String oque, Throwable erro) {
        if (!(erro instanceof ApiException api && api.isUnauthorized())) {
            Toast.error("Não foi possível carregar " + oque, mensagem(erro));
        }
    }

    // ───────── Ações ─────────

    @FXML
    private void onCancelar() {
        Modal.fechar(raiz);
    }

    @FXML
    private void onEnviar() {
        String titulo = tituloField.getText() == null ? "" : tituloField.getText().trim();
        String mensagem = mensagemArea.getText() == null ? "" : mensagemArea.getText().trim();
        boolean valido = true;

        if (titulo.isEmpty()) {
            mostrarErro(tituloErro, tituloField, "Informe um título.");
            valido = false;
        } else {
            limparErro(tituloErro, tituloField);
        }
        if (mensagem.isEmpty()) {
            mostrarErro(mensagemErro, mensagemArea, "Escreva a mensagem do comunicado.");
            valido = false;
        } else {
            limparErro(mensagemErro, mensagemArea);
        }

        String tipoAlvo = tipoAlvoCombo.getValue() == null ? GERAL : tipoAlvoCombo.getValue();
        String alvoId = null;
        if (!GERAL.equals(tipoAlvo)) {
            alvoId = idDoAlvoSelecionado(tipoAlvo);
            if (alvoId == null) {
                mostrarErroAlvo("Selecione o destino do comunicado.");
                valido = false;
            } else {
                limparErroAlvo();
            }
        } else {
            limparErroAlvo();
        }

        if (!valido) {
            return;
        }

        String alvoIdFinal = alvoId;
        enviarBtn.setDisable(true);
        AsyncRunner.runVoid(
                () -> NotificacaoService.enviarComunicado(titulo, mensagem, tipoAlvo, alvoIdFinal),
                () -> {
                    Toast.success("Comunicado enviado!");
                    Modal.fechar(raiz);
                },
                erro -> {
                    enviarBtn.setDisable(false);
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível enviar o comunicado", mensagem(erro));
                    }
                });
    }

    private String idDoAlvoSelecionado(String tipoAlvo) {
        return switch (tipoAlvo) {
            case CURSO -> cursoCombo.getValue() == null ? null : cursoCombo.getValue().id();
            case TURMA -> turmaCombo.getValue() == null ? null : turmaCombo.getValue().id();
            case CLUBE -> clubeCombo.getValue() == null ? null : clubeCombo.getValue().id();
            default -> null;
        };
    }

    // ───────── Auxiliares ─────────

    private static String descreverTurma(TurmaResumo t) {
        StringBuilder sb = new StringBuilder();
        if (t.codigoTurma() != null && !t.codigoTurma().isBlank()) {
            sb.append(t.codigoTurma()).append(" — ");
        }
        sb.append(t.disciplinaNome() == null ? "Turma" : t.disciplinaNome());
        if (t.semestre() != null && !t.semestre().isBlank()) {
            sb.append(" (").append(t.semestre()).append(')');
        }
        return sb.toString();
    }

    private static <T> StringConverter<T> conversor(java.util.function.Function<T, String> rotulo) {
        return new StringConverter<>() {
            @Override
            public String toString(T item) {
                return rotulo.apply(item);
            }

            @Override
            public T fromString(String texto) {
                return null;
            }
        };
    }

    private static void mostrar(VBox box, boolean visivel) {
        box.setManaged(visivel);
        box.setVisible(visivel);
    }

    private void mostrarErro(Label erro, javafx.scene.control.Control campo, String texto) {
        erro.setText(texto);
        erro.setManaged(true);
        erro.setVisible(true);
        if (!campo.getStyleClass().contains("has-error")) {
            campo.getStyleClass().add("has-error");
        }
    }

    private void limparErro(Label erro, javafx.scene.control.Control campo) {
        erro.setManaged(false);
        erro.setVisible(false);
        campo.getStyleClass().remove("has-error");
    }

    private void mostrarErroAlvo(String texto) {
        alvoErro.setText(texto);
        alvoErro.setManaged(true);
        alvoErro.setVisible(true);
    }

    private void limparErroAlvo() {
        alvoErro.setManaged(false);
        alvoErro.setVisible(false);
    }

    private static String mensagem(Throwable erro) {
        if (erro instanceof ApiException api) {
            return api.getMessage();
        }
        return "Tente novamente em instantes.";
    }
}
