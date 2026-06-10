package com.henrique.ifconecta.desktop.controller;

import com.henrique.ifconecta.desktop.Api;
import com.henrique.ifconecta.desktop.App;
import com.henrique.ifconecta.desktop.model.PostResumo;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Tela inicial da area logada. Mostra a lista de posts do campus e deixa
 * curtir, abrir os comentarios e criar um post novo.
 */
public class TimelineController {

    @FXML private VBox lista;

    @FXML
    public void initialize() {
        carregarPosts();
    }

    /** Busca os posts no servidor (em thread de fundo) e mostra na tela. */
    private void carregarPosts() {
        Task<PostResumo[]> tarefa = new Task<>() {
            @Override
            protected PostResumo[] call() {
                return Api.timeline();
            }
        };
        tarefa.setOnSucceeded(evento -> mostrarPosts(tarefa.getValue()));
        tarefa.setOnFailed(evento -> App.erro(tarefa.getException()));
        new Thread(tarefa).start();
    }

    /** Limpa a lista e cria um card para cada post. */
    private void mostrarPosts(PostResumo[] posts) {
        lista.getChildren().clear();
        if (posts.length == 0) {
            lista.getChildren().add(new Label("Ainda nao ha posts. Seja o primeiro a postar!"));
            return;
        }
        for (PostResumo post : posts) {
            lista.getChildren().add(criarCard(post));
        }
    }

    /** Monta o cartao visual de um post. */
    private VBox criarCard(PostResumo post) {
        Label autor = new Label(post.autorNome());
        autor.setStyle("-fx-font-weight: bold;");

        Label texto = new Label(post.conteudo());
        texto.setWrapText(true);

        Button curtir = new Button("Curtir (" + post.qtdUpvotes() + ")");
        curtir.setOnAction(evento -> darUpvote(post));

        Button comentarios = new Button("Comentarios (" + post.qtdComentarios() + ")");
        comentarios.setOnAction(evento -> abrirDetalhe(post));

        HBox acoes = new HBox(8, curtir, comentarios);

        VBox card = new VBox(6, autor, texto, acoes);
        card.getStyleClass().add("card");
        return card;
    }

    /** Curte/descurte e recarrega a lista para atualizar a contagem. */
    private void darUpvote(PostResumo post) {
        Task<Void> tarefa = new Task<>() {
            @Override
            protected Void call() {
                Api.upvote(post.id());
                return null;
            }
        };
        tarefa.setOnSucceeded(evento -> carregarPosts());
        tarefa.setOnFailed(evento -> App.erro(tarefa.getException()));
        new Thread(tarefa).start();
    }

    /** Abre a tela de detalhe do post (passa o id pelo App.parametro). */
    private void abrirDetalhe(PostResumo post) {
        App.parametro = post.id();
        App.mostrarConteudo("PostDetalhe");
    }

    @FXML
    private void onCriarPost() {
        App.mostrarConteudo("CriarPost");
    }
}
