package com.henrique.ifconecta.desktop.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.henrique.ifconecta.desktop.core.AsyncRunner;
import com.henrique.ifconecta.desktop.core.http.ApiException;
import com.henrique.ifconecta.desktop.model.NotificacaoResumo;
import com.henrique.ifconecta.desktop.service.NotificacaoService;
import com.henrique.ifconecta.desktop.ui.Format;
import com.henrique.ifconecta.desktop.ui.Icons;
import com.henrique.ifconecta.desktop.ui.Toast;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Notificações — lista os comunicados recebidos pelo usuário (GET /api/comunicados/minhas).
 * Permite marcar uma ou todas as notificações como lidas.
 */
public class NotificacoesController {

    private static final int TAMANHO_PAGINA = 10;

    @FXML private Button marcarTodasBtn;
    @FXML private VBox listaBox;
    @FXML private StackPane loadingBox;
    @FXML private Button carregarMaisBtn;

    private final List<NotificacaoResumo> notificacoes = new ArrayList<>();
    private final Set<String> emAndamento = new HashSet<>();
    private int paginaAtual;
    private int totalPaginas;

    @FXML
    private void initialize() {
        carregar(0);
    }

    // ───────── Carregamento ─────────

    private void carregar(int pagina) {
        boolean primeira = pagina == 0;
        if (primeira) {
            mostrarLoading(true);
        }
        carregarMaisBtn.setDisable(true);

        AsyncRunner.run(
                () -> NotificacaoService.listar(pagina, TAMANHO_PAGINA),
                page -> {
                    mostrarLoading(false);
                    carregarMaisBtn.setDisable(false);
                    if (primeira) {
                        notificacoes.clear();
                    }
                    notificacoes.addAll(page.itens());
                    paginaAtual = page.paginaAtual();
                    totalPaginas = page.totalPaginas();
                    renderizar();
                },
                erro -> {
                    mostrarLoading(false);
                    carregarMaisBtn.setDisable(false);
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível carregar as notificações", mensagem(erro));
                    }
                    renderizar();
                });
    }

    @FXML
    private void onCarregarMais() {
        carregar(paginaAtual + 1);
    }

    @FXML
    private void onMarcarTodas() {
        if (notificacoes.isEmpty()) {
            return;
        }
        marcarTodasBtn.setDisable(true);
        AsyncRunner.runVoid(
                NotificacaoService::marcarTodasComoLidas,
                () -> {
                    marcarTodasBtn.setDisable(false);
                    Toast.success("Todas as notificações foram marcadas como lidas.");
                    carregar(0);
                },
                erro -> {
                    marcarTodasBtn.setDisable(false);
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível marcar as notificações", mensagem(erro));
                    }
                });
    }

    private void marcarComoLida(NotificacaoResumo notificacao) {
        if (emAndamento.contains(notificacao.id())) {
            return;
        }
        emAndamento.add(notificacao.id());
        AsyncRunner.runVoid(
                () -> NotificacaoService.marcarComoLida(notificacao.id()),
                () -> {
                    emAndamento.remove(notificacao.id());
                    carregar(0);
                },
                erro -> {
                    emAndamento.remove(notificacao.id());
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível marcar como lida", mensagem(erro));
                    }
                });
    }

    // ───────── Render ─────────

    private void renderizar() {
        listaBox.getChildren().clear();
        if (notificacoes.isEmpty()) {
            listaBox.getChildren().add(estadoVazio());
        } else {
            for (NotificacaoResumo notificacao : notificacoes) {
                listaBox.getChildren().add(cartao(notificacao));
            }
        }
        boolean temMais = paginaAtual + 1 < totalPaginas;
        carregarMaisBtn.setVisible(temMais);
        carregarMaisBtn.setManaged(temMais);
    }

    private VBox cartao(NotificacaoResumo notificacao) {
        VBox cartao = new VBox(5);
        cartao.getStyleClass().add("list-card");

        // Linha do título: título + chip "Nova" (quando não lida)
        HBox linhaTitulo = new HBox(8);
        linhaTitulo.setAlignment(Pos.CENTER_LEFT);

        Label titulo = new Label(notificacao.titulo());
        titulo.getStyleClass().add("item-title");
        titulo.setWrapText(true);
        if (!notificacao.lida()) {
            titulo.setStyle("-fx-font-weight: bold;");
        }
        linhaTitulo.getChildren().add(titulo);

        if (!notificacao.lida()) {
            Label nova = new Label("Nova");
            nova.getStyleClass().addAll("chip", "chip-info");
            linhaTitulo.getChildren().add(nova);
        }

        // Meta: remetente + tempo
        String remetente = (notificacao.remetenteNome() == null
                || notificacao.remetenteNome().isBlank())
                ? "Sistema" : notificacao.remetenteNome();
        Label meta = new Label("De " + remetente + " · "
                + Format.timeAgo(notificacao.dataCriacao()));
        meta.getStyleClass().add("item-meta");

        // Mensagem
        Label mensagem = new Label(notificacao.mensagem());
        mensagem.getStyleClass().add("item-sub");
        mensagem.setWrapText(true);

        cartao.getChildren().addAll(linhaTitulo, meta, mensagem);

        // Ação: marcar como lida (apenas quando não lida)
        if (!notificacao.lida()) {
            HBox acoes = new HBox();
            acoes.setAlignment(Pos.CENTER_LEFT);
            Region espaco = new Region();
            HBox.setHgrow(espaco, javafx.scene.layout.Priority.ALWAYS);

            Button marcar = new Button("Marcar como lida");
            marcar.getStyleClass().addAll("btn", "btn-ghost", "btn-sm");
            marcar.setOnAction(e -> marcarComoLida(notificacao));

            acoes.getChildren().addAll(espaco, marcar);
            cartao.getChildren().add(acoes);
        }

        return cartao;
    }

    private VBox estadoVazio() {
        VBox caixa = new VBox(8);
        caixa.getStyleClass().add("empty");

        StackPane icone = new StackPane(Icons.of("fth-bell", 24));
        icone.getStyleClass().add("empty-icon");

        Label titulo = new Label("Nenhuma notificação");
        titulo.getStyleClass().add("empty-title");

        Label msg = new Label("Você está em dia — não há comunicados por aqui.");
        msg.getStyleClass().add("empty-msg");
        msg.setWrapText(true);

        caixa.getChildren().addAll(icone, titulo, msg);
        return caixa;
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
