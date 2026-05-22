package com.henrique.ifconecta.desktop.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.henrique.ifconecta.desktop.core.AsyncRunner;
import com.henrique.ifconecta.desktop.core.Session;
import com.henrique.ifconecta.desktop.core.http.ApiException;
import com.henrique.ifconecta.desktop.model.MeuPerfil;
import com.henrique.ifconecta.desktop.model.Pagina;
import com.henrique.ifconecta.desktop.model.TurmaResumo;
import com.henrique.ifconecta.desktop.service.TurmaService;
import com.henrique.ifconecta.desktop.ui.Icons;
import com.henrique.ifconecta.desktop.ui.Modal;
import com.henrique.ifconecta.desktop.ui.Toast;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Tela Acadêmico — port de academico.jsx do front web.
 * Abas: minhas turmas, todas (paginada), turmas lecionadas e solicitações.
 */
public class AcademicoController {

    private static final int TAMANHO_PAGINA = 12;

    private enum Aba { MINHAS, TODAS, LECIONO, SOLICITACOES }

    @FXML private Button tabMinhas;
    @FXML private Button tabTodas;
    @FXML private Button tabLeciono;
    @FXML private Button tabSolicitacoes;
    @FXML private VBox listaBox;
    @FXML private StackPane loadingBox;
    @FXML private Button carregarMaisBtn;

    private Aba abaAtual = Aba.MINHAS;
    private final List<TurmaResumo> turmas = new ArrayList<>();
    private int paginaAtual;
    private int totalPaginas;

    @FXML
    private void initialize() {
        MeuPerfil me = Session.get().me();
        boolean professor = me != null && "PROFESSOR".equalsIgnoreCase(me.tipo());
        tabLeciono.setVisible(professor);
        tabLeciono.setManaged(professor);
        tabSolicitacoes.setVisible(professor);
        tabSolicitacoes.setManaged(professor);
        selecionarAba(Aba.MINHAS);
    }

    // ───────── Abas ─────────

    @FXML private void onTabMinhas()       { selecionarAba(Aba.MINHAS); }
    @FXML private void onTabTodas()        { selecionarAba(Aba.TODAS); }
    @FXML private void onTabLeciono()      { selecionarAba(Aba.LECIONO); }
    @FXML private void onTabSolicitacoes() { selecionarAba(Aba.SOLICITACOES); }

    private void selecionarAba(Aba aba) {
        abaAtual = aba;
        tabMinhas.getStyleClass().remove("active");
        tabTodas.getStyleClass().remove("active");
        tabLeciono.getStyleClass().remove("active");
        tabSolicitacoes.getStyleClass().remove("active");
        switch (aba) {
            case MINHAS       -> tabMinhas.getStyleClass().add("active");
            case TODAS        -> tabTodas.getStyleClass().add("active");
            case LECIONO      -> tabLeciono.getStyleClass().add("active");
            case SOLICITACOES -> tabSolicitacoes.getStyleClass().add("active");
        }
        recarregar();
    }

    /** Recarrega a aba atual a partir do início. */
    public void recarregar() {
        carregar(true);
    }

    // ───────── Carregamento ─────────

    private void carregar(boolean primeira) {
        if (primeira) {
            turmas.clear();
            paginaAtual = 0;
            totalPaginas = 0;
            mostrarLoading(true);
        }
        carregarMaisBtn.setDisable(true);

        if (abaAtual == Aba.TODAS) {
            final int pagina = primeira ? 0 : paginaAtual + 1;
            AsyncRunner.run(
                    () -> TurmaService.listar(pagina, TAMANHO_PAGINA),
                    page -> {
                        mostrarLoading(false);
                        carregarMaisBtn.setDisable(false);
                        turmas.addAll(page.itens());
                        paginaAtual = page.paginaAtual();
                        totalPaginas = page.totalPaginas();
                        renderizar();
                    },
                    erro -> aoFalhar(erro));
        } else {
            Supplier<List<TurmaResumo>> fonte = switch (abaAtual) {
                case MINHAS       -> TurmaService::minhas;
                case LECIONO      -> TurmaService::lecionadas;
                case SOLICITACOES -> TurmaService::solicitacoesParaAprovar;
                default           -> TurmaService::minhas;
            };
            AsyncRunner.run(
                    fonte,
                    lista -> {
                        mostrarLoading(false);
                        carregarMaisBtn.setDisable(false);
                        turmas.addAll(lista);
                        renderizar();
                    },
                    erro -> aoFalhar(erro));
        }
    }

    private void aoFalhar(Throwable erro) {
        mostrarLoading(false);
        carregarMaisBtn.setDisable(false);
        if (!(erro instanceof ApiException api && api.isUnauthorized())) {
            Toast.error("Não foi possível carregar as turmas", mensagem(erro));
        }
        renderizar();
    }

    @FXML
    private void onCarregarMais() {
        carregar(false);
    }

    @FXML
    private void onSolicitarTurma() {
        Modal.abrir("criar-turma", "Solicitar turma",
                (CriarTurmaController c) -> c.configurar(this::recarregar));
    }

    // ───────── Render ─────────

    private void renderizar() {
        listaBox.getChildren().clear();
        if (turmas.isEmpty()) {
            listaBox.getChildren().add(estadoVazio());
        } else {
            for (TurmaResumo turma : turmas) {
                listaBox.getChildren().add(cartaoTurma(turma));
            }
        }
        boolean temMais = abaAtual == Aba.TODAS && paginaAtual + 1 < totalPaginas;
        carregarMaisBtn.setVisible(temMais);
        carregarMaisBtn.setManaged(temMais);
    }

    private VBox cartaoTurma(TurmaResumo turma) {
        VBox cartao = new VBox(5);
        cartao.getStyleClass().add("list-card");

        // Linha de título + chip de status
        HBox linhaTitulo = new HBox(8);
        linhaTitulo.setAlignment(Pos.CENTER_LEFT);
        Label titulo = new Label(turma.codigoTurma() + " · " + turma.disciplinaNome());
        titulo.getStyleClass().add("item-title");
        titulo.setWrapText(true);
        HBox.setHgrow(titulo, Priority.ALWAYS);
        titulo.setMaxWidth(Double.MAX_VALUE);
        linhaTitulo.getChildren().addAll(titulo, chipStatus(turma));

        // Subtítulo e meta
        Label sub = new Label(turma.cursoSigla() + " · " + turma.semestre()
                + " · Prof. " + turma.professorNome());
        sub.getStyleClass().add("item-sub");
        sub.setWrapText(true);

        Label meta = new Label(turma.qtdMatriculados() + " matriculados · "
                + turma.cargaHoraria() + "h");
        meta.getStyleClass().add("item-meta");

        cartao.getChildren().addAll(linhaTitulo, sub, meta);

        HBox acoes = acoes(turma);
        if (acoes != null) {
            cartao.getChildren().add(acoes);
        }
        return cartao;
    }

    private Label chipStatus(TurmaResumo turma) {
        Label chip = new Label();
        chip.getStyleClass().add("chip");
        if (turma.isAtiva()) {
            chip.setText("Ativa");
            chip.getStyleClass().add("chip-success");
        } else if (turma.isSolicitacao()) {
            chip.setText("Solicitação");
            chip.getStyleClass().add("chip-info");
        } else if ("REJEITADA".equalsIgnoreCase(turma.status())) {
            chip.setText("Rejeitada");
            chip.getStyleClass().add("chip-danger");
        } else {
            chip.setText(turma.status());
            chip.getStyleClass().add("chip-muted");
        }
        return chip;
    }

    private HBox acoes(TurmaResumo turma) {
        HBox barra = new HBox(8);
        barra.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(barra, new javafx.geometry.Insets(4, 0, 0, 0));

        switch (abaAtual) {
            case MINHAS -> {
                Button cancelar = botao("Cancelar matrícula", "btn-outline");
                cancelar.setOnAction(e -> executarAcao(cancelar,
                        () -> TurmaService.cancelarMatricula(turma.id()),
                        "Matrícula cancelada."));
                barra.getChildren().add(cancelar);
            }
            case TODAS -> {
                if (!turma.isAtiva()) {
                    return null;
                }
                Button matricular = botao("Matricular", null);
                matricular.setOnAction(e -> executarAcao(matricular,
                        () -> TurmaService.matricular(turma.id()),
                        "Matrícula realizada!"));
                barra.getChildren().add(matricular);
            }
            case SOLICITACOES -> {
                Button aprovar = botao("Aprovar", null);
                aprovar.setOnAction(e -> executarAcao(aprovar,
                        () -> TurmaService.aprovar(turma.id()),
                        "Solicitação aprovada."));
                Button rejeitar = botao("Rejeitar", "btn-outline");
                rejeitar.setOnAction(e -> executarAcao(rejeitar,
                        () -> TurmaService.rejeitar(turma.id()),
                        "Solicitação rejeitada."));
                barra.getChildren().addAll(aprovar, rejeitar);
            }
            default -> {
                return null;
            }
        }
        return barra;
    }

    private Button botao(String texto, String extraClasse) {
        Button btn = new Button(texto);
        btn.getStyleClass().addAll("btn", "btn-sm");
        if (extraClasse != null) {
            btn.getStyleClass().add(extraClasse);
        }
        return btn;
    }

    private void executarAcao(Button origem, Runnable acao, String mensagemSucesso) {
        origem.setDisable(true);
        AsyncRunner.runVoid(
                acao,
                () -> {
                    Toast.success(mensagemSucesso);
                    recarregar();
                },
                erro -> {
                    origem.setDisable(false);
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível concluir a ação", mensagem(erro));
                    }
                });
    }

    private VBox estadoVazio() {
        VBox caixa = new VBox(8);
        caixa.getStyleClass().add("empty");

        StackPane icone = new StackPane(Icons.of("fth-book-open", 24));
        icone.getStyleClass().add("empty-icon");

        Label titulo = new Label(tituloVazio());
        titulo.getStyleClass().add("empty-title");

        Label msg = new Label(mensagemVazio());
        msg.getStyleClass().add("empty-msg");
        msg.setWrapText(true);

        caixa.getChildren().addAll(icone, titulo, msg);
        return caixa;
    }

    private String tituloVazio() {
        return switch (abaAtual) {
            case MINHAS       -> "Você ainda não tem matrículas";
            case TODAS        -> "Nenhuma turma cadastrada";
            case LECIONO      -> "Você não leciona nenhuma turma";
            case SOLICITACOES -> "Nenhuma solicitação pendente";
        };
    }

    private String mensagemVazio() {
        return switch (abaAtual) {
            case MINHAS       -> "Matricule-se em uma turma pela aba \"Todas\".";
            case TODAS        -> "Quando houver turmas ativas, elas aparecem aqui.";
            case LECIONO      -> "As turmas que você leciona aparecem aqui.";
            case SOLICITACOES -> "Solicitações de turma para você aprovar aparecem aqui.";
        };
    }

    // ───────── Auxiliares ─────────

    private void mostrarLoading(boolean visivel) {
        loadingBox.setVisible(visivel);
        loadingBox.setManaged(visivel);
    }

    private static String mensagem(Throwable erro) {
        if (erro instanceof ApiException api) {
            return api.getMessage();
        }
        return "Tente novamente em instantes.";
    }
}
