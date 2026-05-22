package com.henrique.ifconecta.desktop.controller;

import java.util.ArrayList;
import java.util.List;

import com.henrique.ifconecta.desktop.core.AsyncRunner;
import com.henrique.ifconecta.desktop.core.Router;
import com.henrique.ifconecta.desktop.core.http.ApiException;
import com.henrique.ifconecta.desktop.model.ClubeResumo;
import com.henrique.ifconecta.desktop.service.ClubeService;
import com.henrique.ifconecta.desktop.ui.Icons;
import com.henrique.ifconecta.desktop.ui.Modal;
import com.henrique.ifconecta.desktop.ui.Toast;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Lista de clubes — "Meus clubes" + "Explorar" (paginado). */
public class ClubesController {

    private static final int TAMANHO_PAGINA = 10;

    @FXML private VBox meusClubesBox;
    @FXML private VBox explorarBox;
    @FXML private StackPane loadingBox;
    @FXML private Button carregarMaisBtn;

    private final List<ClubeResumo> explorar = new ArrayList<>();
    private int paginaAtual;
    private int totalPaginas;

    @FXML
    private void initialize() {
        recarregar();
    }

    void recarregar() {
        carregarMeus();
        carregarExplorar(0);
    }

    // ───────── Carregamento ─────────

    private void carregarMeus() {
        AsyncRunner.run(
                ClubeService::meusClubes,
                this::renderizarMeus,
                erro -> {
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível carregar seus clubes", mensagem(erro));
                    }
                    renderizarMeus(List.of());
                });
    }

    private void carregarExplorar(int pagina) {
        boolean primeira = pagina == 0;
        if (primeira) {
            mostrarLoading(true);
        }
        carregarMaisBtn.setDisable(true);

        AsyncRunner.run(
                () -> ClubeService.listar(pagina, TAMANHO_PAGINA),
                page -> {
                    mostrarLoading(false);
                    carregarMaisBtn.setDisable(false);
                    if (primeira) {
                        explorar.clear();
                    }
                    explorar.addAll(page.itens());
                    paginaAtual = page.paginaAtual();
                    totalPaginas = page.totalPaginas();
                    renderizarExplorar();
                },
                erro -> {
                    mostrarLoading(false);
                    carregarMaisBtn.setDisable(false);
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível carregar os clubes", mensagem(erro));
                    }
                    renderizarExplorar();
                });
    }

    @FXML
    private void onCarregarMais() {
        carregarExplorar(paginaAtual + 1);
    }

    @FXML
    private void onCriarClube() {
        Modal.abrir("criar-clube", "Criar clube",
                (CriarClubeController c) -> c.configurar(this::recarregar));
    }

    // ───────── Render ─────────

    private void renderizarMeus(List<ClubeResumo> clubes) {
        meusClubesBox.getChildren().clear();
        if (clubes == null || clubes.isEmpty()) {
            Label vazio = new Label("Você ainda não participa de clubes.");
            vazio.getStyleClass().add("item-sub");
            meusClubesBox.getChildren().add(vazio);
            return;
        }
        for (ClubeResumo clube : clubes) {
            meusClubesBox.getChildren().add(cartaoClube(clube));
        }
    }

    private void renderizarExplorar() {
        explorarBox.getChildren().clear();
        if (explorar.isEmpty()) {
            explorarBox.getChildren().add(estadoVazio());
        } else {
            for (ClubeResumo clube : explorar) {
                explorarBox.getChildren().add(cartaoClube(clube));
            }
        }
        boolean temMais = paginaAtual + 1 < totalPaginas;
        carregarMaisBtn.setVisible(temMais);
        carregarMaisBtn.setManaged(temMais);
    }

    private VBox cartaoClube(ClubeResumo clube) {
        VBox cartao = new VBox(5);
        cartao.getStyleClass().addAll("list-card", "clickable");

        Label nome = new Label(clube.nome());
        nome.getStyleClass().add("item-title");

        Label descricao = new Label(clube.descricao() == null ? "" : clube.descricao());
        descricao.getStyleClass().add("item-sub");
        descricao.setWrapText(true);

        HBox rodape = new HBox(8);
        rodape.setAlignment(Pos.CENTER_LEFT);

        Label tipo = new Label(rotuloTipo(clube.tipoAcesso()));
        tipo.getStyleClass().addAll("chip", privado(clube.tipoAcesso()) ? "chip-muted" : "chip-info");

        Label membros = new Label(clube.quantidadeMembros() + " membros");
        membros.getStyleClass().add("item-meta");

        rodape.getChildren().addAll(tipo, membros);

        cartao.getChildren().addAll(nome, descricao, rodape);
        cartao.setOnMouseClicked(e -> Router.get().navigate("clube-detalhe", clube.id()));
        return cartao;
    }

    private VBox estadoVazio() {
        VBox caixa = new VBox(8);
        caixa.getStyleClass().add("empty");

        StackPane icone = new StackPane(Icons.of("fth-users", 24));
        icone.getStyleClass().add("empty-icon");

        Label titulo = new Label("Nenhum clube por aqui ainda");
        titulo.getStyleClass().add("empty-title");

        Label msg = new Label("Que tal criar o primeiro clube do campus?");
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

    private static boolean privado(String tipoAcesso) {
        return "PRIVADO".equalsIgnoreCase(tipoAcesso);
    }

    private static String rotuloTipo(String tipoAcesso) {
        return privado(tipoAcesso) ? "Privado" : "Público";
    }

    private static String mensagem(Throwable erro) {
        if (erro instanceof ApiException api) {
            return api.getMessage();
        }
        return "Tente novamente em instantes.";
    }
}
