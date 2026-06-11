package com.henrique.ifconecta.desktop.controller;

import com.henrique.ifconecta.desktop.Api;
import com.henrique.ifconecta.desktop.App;
import com.henrique.ifconecta.desktop.model.NotificacaoResumo;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class NotificacoesController {

    @FXML private VBox lista;

    @FXML
    public void initialize() {
        carregar();
    }

    private void carregar() {
        Task<NotificacaoResumo[]> tarefa = new Task<>() {
            @Override
            protected NotificacaoResumo[] call() {
                return Api.minhasNotificacoes();
            }
        };
        tarefa.setOnSucceeded(evento -> {
            lista.getChildren().clear();
            NotificacaoResumo[] ns = tarefa.getValue();
            if (ns.length == 0) {
                lista.getChildren().add(new Label("Nenhuma notificacao."));
            } else {
                for (NotificacaoResumo n : ns) {
                    lista.getChildren().add(criarCard(n));
                }
            }
        });
        tarefa.setOnFailed(evento -> App.erro(tarefa.getException()));
        new Thread(tarefa).start();
    }

    private VBox criarCard(NotificacaoResumo n) {
        Label titulo = new Label(n.titulo());
        // negrito so quando ainda nao foi lida, pra destacar
        if (!n.lida()) {
            titulo.setStyle("-fx-font-weight: bold;");
        }
        Label meta = new Label("De " + n.remetenteNome());
        meta.getStyleClass().add("subtitulo");
        Label msg = new Label(n.mensagem());
        msg.setWrapText(true);

        VBox card = new VBox(6, titulo, meta, msg);
        card.getStyleClass().add("card");

        // so mostra o botao se ainda nao estiver lida
        if (!n.lida()) {
            Button lida = new Button("Marcar como lida");
            lida.setOnAction(e -> marcar(n));
            card.getChildren().add(lida);
        }
        return card;
    }

    private void marcar(NotificacaoResumo n) {
        Task<Void> tarefa = new Task<>() {
            @Override
            protected Void call() {
                Api.marcarLida(n.id());
                return null;
            }
        };
        tarefa.setOnSucceeded(evento -> carregar());
        tarefa.setOnFailed(evento -> App.erro(tarefa.getException()));
        new Thread(tarefa).start();
    }

    @FXML
    private void onMarcarTodas() {
        Task<Void> tarefa = new Task<>() {
            @Override
            protected Void call() {
                Api.marcarTodasLidas();
                return null;
            }
        };
        tarefa.setOnSucceeded(evento -> carregar());
        tarefa.setOnFailed(evento -> App.erro(tarefa.getException()));
        new Thread(tarefa).start();
    }
}
