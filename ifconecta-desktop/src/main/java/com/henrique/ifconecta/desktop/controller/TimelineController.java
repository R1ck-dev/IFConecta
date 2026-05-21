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
import com.henrique.ifconecta.desktop.ui.Format;
import com.henrique.ifconecta.desktop.ui.Icons;
import com.henrique.ifconecta.desktop.ui.Toast;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Timeline — port de TimelinePage (ifconecta-web/src/pages/timeline.jsx).
 * Lista os posts de GET /api/posts (paginado) e permite dar upvote.
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
        Toast.info("Criar post", "A criação de posts chega na próxima fase da migração.");
    }

    // ───────── Render ─────────

    private void renderizarFeed() {
        feedBox.getChildren().clear();
        if (posts.isEmpty()) {
            feedBox.getChildren().add(estadoVazio());
        } else {
            for (PostResumo post : posts) {
                feedBox.getChildren().add(cartaoPost(post));
            }
        }
        boolean temMais = paginaAtual + 1 < totalPaginas;
        carregarMaisBtn.setVisible(temMais);
        carregarMaisBtn.setManaged(temMais);
    }

    private VBox cartaoPost(PostResumo post) {
        VBox cartao = new VBox(10);
        cartao.getStyleClass().add("post");

        // Cabeçalho: avatar + autor + tempo
        boolean anonimo = post.autorNome() == null || post.autorNome().isBlank();
        HBox cabecalho = new HBox(11);
        cabecalho.setAlignment(Pos.CENTER_LEFT);

        VBox autor = new VBox(2);
        Label nome = new Label(anonimo ? "Anônimo" : post.autorNome());
        nome.getStyleClass().add("post-author-name");
        Label tempo = new Label(Format.timeAgo(post.dataCriacao()));
        tempo.getStyleClass().add("post-author-meta");
        autor.getChildren().addAll(nome, tempo);

        cabecalho.getChildren().addAll(new Avatar(anonimo ? "" : post.autorNome(), 38), autor);

        // Corpo
        Label corpo = new Label(post.conteudo());
        corpo.getStyleClass().add("post-body");
        corpo.setWrapText(true);

        // Rodapé: upvote + comentários
        HBox rodape = new HBox(6);
        rodape.setAlignment(Pos.CENTER_LEFT);

        Button upvote = new Button(String.valueOf(post.qtdUpvotes()));
        upvote.getStyleClass().add("post-action");
        if (post.jaDeiUpvote()) {
            upvote.getStyleClass().add("active");
        }
        upvote.setGraphic(Icons.of("fth-arrow-up", 15));
        upvote.setOnAction(e -> darUpvote(post));

        Button comentarios = new Button(String.valueOf(post.qtdComentarios()));
        comentarios.getStyleClass().add("post-action");
        comentarios.setGraphic(Icons.of("fth-message-square", 15));
        comentarios.setOnAction(e ->
                Toast.info("Comentários", "O detalhe do post chega na próxima fase da migração."));

        rodape.getChildren().addAll(upvote, comentarios);

        cartao.getChildren().addAll(cabecalho, corpo, rodape);
        return cartao;
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
