package com.henrique.ifconecta.desktop.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.henrique.ifconecta.desktop.core.AsyncRunner;
import com.henrique.ifconecta.desktop.core.Session;
import com.henrique.ifconecta.desktop.core.http.ApiException;
import com.henrique.ifconecta.desktop.model.MeuPerfil;
import com.henrique.ifconecta.desktop.model.PostResumo;
import com.henrique.ifconecta.desktop.service.PostService;
import com.henrique.ifconecta.desktop.ui.Avatar;
import com.henrique.ifconecta.desktop.ui.Icons;
import com.henrique.ifconecta.desktop.ui.Modal;
import com.henrique.ifconecta.desktop.ui.PostCard;
import com.henrique.ifconecta.desktop.ui.Toast;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Timeline — port de TimelinePage (ifconecta-web/src/pages/timeline.jsx).
 * Lista os posts de GET /api/posts (paginado), permite criar post, abrir o
 * detalhe com comentários e dar upvote.
 */
public class TimelineController {

    private static final int TAMANHO_PAGINA = 10;

    @FXML private StackPane composerAvatar;
    @FXML private Label composerFaux;
    @FXML private VBox feedBox;
    @FXML private StackPane loadingBox;
    @FXML private Button carregarMaisBtn;

    private final List<PostResumo> posts = new ArrayList<>();
    private final Set<String> upvoteEmAndamento = new HashSet<>();
    private int paginaAtual;
    private int totalPaginas;

    @FXML
    private void initialize() {
        MeuPerfil me = Session.get().me();
        composerAvatar.getChildren().setAll(new Avatar(me == null ? "" : me.nome(), 38));
        composerFaux.setMaxWidth(Double.MAX_VALUE);
        composerFaux.setText("No que você está pensando, "
                + (me == null ? "colega" : me.primeiroNome()) + "?");
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
                () -> PostService.listarTimeline(pagina, TAMANHO_PAGINA),
                page -> {
                    mostrarLoading(false);
                    carregarMaisBtn.setDisable(false);
                    if (primeira) {
                        posts.clear();
                    }
                    posts.addAll(page.itens());
                    paginaAtual = page.paginaAtual();
                    totalPaginas = page.totalPaginas();
                    renderizarFeed();
                },
                erro -> {
                    mostrarLoading(false);
                    carregarMaisBtn.setDisable(false);
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível carregar a timeline", mensagem(erro));
                    }
                    renderizarFeed();
                });
    }

    @FXML
    private void onCarregarMais() {
        carregar(paginaAtual + 1);
    }

    @FXML
    private void onComposer() {
        Modal.abrir("criar-post", "Novo post",
                (CriarPostController c) -> c.configurar(null, () -> carregar(0)));
    }

    // ───────── Render ─────────

    private void renderizarFeed() {
        feedBox.getChildren().clear();
        if (posts.isEmpty()) {
            feedBox.getChildren().add(estadoVazio());
        } else {
            for (PostResumo post : posts) {
                feedBox.getChildren().add(PostCard.criar(post, this::darUpvote, this::abrirDetalhe));
            }
        }
        boolean temMais = paginaAtual + 1 < totalPaginas;
        carregarMaisBtn.setVisible(temMais);
        carregarMaisBtn.setManaged(temMais);
    }

    private void abrirDetalhe(PostResumo post) {
        Modal.abrir("post-detalhe", "Post",
                (PostDetalheController c) -> c.carregar(post.id()));
    }

    private VBox estadoVazio() {
        VBox caixa = new VBox(8);
        caixa.getStyleClass().add("empty");

        StackPane icone = new StackPane(Icons.of("fth-message-square", 24));
        icone.getStyleClass().add("empty-icon");

        Label titulo = new Label("Nenhum post por aqui ainda");
        titulo.getStyleClass().add("empty-title");

        Label msg = new Label("Quando houver atividade no campus, ela aparece aqui.");
        msg.getStyleClass().add("empty-msg");
        msg.setWrapText(true);

        caixa.getChildren().addAll(icone, titulo, msg);
        return caixa;
    }

    // ───────── Upvote (atualização otimista) ─────────

    private void darUpvote(PostResumo post) {
        if (upvoteEmAndamento.contains(post.id())) {
            return;
        }
        int indice = indiceDe(post.id());
        if (indice < 0) {
            return;
        }
        upvoteEmAndamento.add(post.id());
        posts.set(indice, post.comUpvoteAlternado());
        renderizarFeed();

        AsyncRunner.runVoid(
                () -> PostService.upvote(post.id()),
                () -> upvoteEmAndamento.remove(post.id()),
                erro -> {
                    upvoteEmAndamento.remove(post.id());
                    int i = indiceDe(post.id());
                    if (i >= 0) {
                        posts.set(i, post);
                        renderizarFeed();
                    }
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível registrar o upvote", mensagem(erro));
                    }
                });
    }

    private int indiceDe(String postId) {
        for (int i = 0; i < posts.size(); i++) {
            if (posts.get(i).id().equals(postId)) {
                return i;
            }
        }
        return -1;
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
