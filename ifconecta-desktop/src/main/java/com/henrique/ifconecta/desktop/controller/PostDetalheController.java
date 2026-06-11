package com.henrique.ifconecta.desktop.controller;

import com.henrique.ifconecta.desktop.Api;
import com.henrique.ifconecta.desktop.App;
import com.henrique.ifconecta.desktop.model.Comentario;
import com.henrique.ifconecta.desktop.model.PostDetalhe;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class PostDetalheController {

    @FXML private VBox postBox;
    @FXML private VBox comentariosBox;
    @FXML private TextField comentarioField;

    // guarda o id do post que veio da tela anterior
    private String postId;

    @FXML
    public void initialize() {
        postId = App.parametro;
        carregar();
    }

    private void carregar() {
        Task<PostDetalhe> tarefa = new Task<>() {
            @Override
            protected PostDetalhe call() {
                return Api.detalhePost(postId);
            }
        };
        tarefa.setOnSucceeded(evento -> mostrar(tarefa.getValue()));
        tarefa.setOnFailed(evento -> App.erro(tarefa.getException()));
        new Thread(tarefa).start();
    }

    private void mostrar(PostDetalhe d) {
        postBox.getChildren().clear();
        Label autor = new Label(d.anonimo() ? "Anonimo" : d.autorNome());
        autor.setStyle("-fx-font-weight: bold;");
        Label corpo = new Label(d.conteudo());
        corpo.setWrapText(true);
        Label up = new Label(d.qtdUpvotes() + " curtidas");
        postBox.getChildren().addAll(autor, corpo, up);

        comentariosBox.getChildren().clear();
        if (d.comentarios().isEmpty()) {
            comentariosBox.getChildren().add(new Label("Nenhum comentario ainda."));
        } else {
            for (Comentario c : d.comentarios()) {
                Label cAutor = new Label(c.autorNome());
                cAutor.setStyle("-fx-font-weight: bold;");
                Label cTexto = new Label(c.conteudo());
                cTexto.setWrapText(true);
                VBox card = new VBox(4, cAutor, cTexto);
                card.getStyleClass().add("card");
                comentariosBox.getChildren().add(card);
            }
        }
    }

    @FXML
    private void onComentar() {
        String texto = comentarioField.getText().trim();
        if (texto.isEmpty()) {
            App.avisar("Escreva um comentario.");
            return;
        }
        Task<Void> tarefa = new Task<>() {
            @Override
            protected Void call() {
                Api.comentar(postId, texto);
                return null;
            }
        };
        tarefa.setOnSucceeded(evento -> {
            comentarioField.clear();
            carregar(); // recarrega para mostrar o novo comentario
        });
        tarefa.setOnFailed(evento -> App.erro(tarefa.getException()));
        new Thread(tarefa).start();
    }

    @FXML
    private void onVoltar() {
        App.mostrarConteudo("Timeline");
    }
}
