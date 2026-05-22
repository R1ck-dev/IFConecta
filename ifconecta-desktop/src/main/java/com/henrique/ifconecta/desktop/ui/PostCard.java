package com.henrique.ifconecta.desktop.ui;

import java.util.function.Consumer;

import com.henrique.ifconecta.desktop.model.PostResumo;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** Constrói o cartão visual de um post — reutilizado pela Timeline e pela tela de Clube. */
public final class PostCard {

    private PostCard() {
    }

    /**
     * @param post         dados do post
     * @param onUpvote     ação ao clicar no botão de upvote (pode ser nulo)
     * @param onComentarios ação ao clicar no botão de comentários (pode ser nulo)
     */
    public static VBox criar(PostResumo post, Consumer<PostResumo> onUpvote, Consumer<PostResumo> onComentarios) {
        VBox cartao = new VBox(10);
        cartao.getStyleClass().add("post");

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

        Label corpo = new Label(post.conteudo());
        corpo.getStyleClass().add("post-body");
        corpo.setWrapText(true);

        HBox rodape = new HBox(6);
        rodape.setAlignment(Pos.CENTER_LEFT);

        Button upvote = new Button(String.valueOf(post.qtdUpvotes()));
        upvote.getStyleClass().add("post-action");
        if (post.jaDeiUpvote()) {
            upvote.getStyleClass().add("active");
        }
        upvote.setGraphic(Icons.of("fth-arrow-up", 15));
        if (onUpvote != null) {
            upvote.setOnAction(e -> onUpvote.accept(post));
        }

        Button comentarios = new Button(String.valueOf(post.qtdComentarios()));
        comentarios.getStyleClass().add("post-action");
        comentarios.setGraphic(Icons.of("fth-message-square", 15));
        if (onComentarios != null) {
            comentarios.setOnAction(e -> onComentarios.accept(post));
        }

        rodape.getChildren().addAll(upvote, comentarios);
        cartao.getChildren().addAll(cabecalho, corpo, rodape);
        return cartao;
    }
}
