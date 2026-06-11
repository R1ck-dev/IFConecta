package com.henrique.ifconecta.desktop.controller;

import com.henrique.ifconecta.desktop.Api;
import com.henrique.ifconecta.desktop.App;
import com.henrique.ifconecta.desktop.model.ClubeResumo;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ClubesController {

    @FXML private VBox meusBox;
    @FXML private VBox explorarBox;

    @FXML
    public void initialize() {
        carregarMeus();
        carregarTodos();
    }

    // Clubes em que o usuario ja participa
    private void carregarMeus() {
        Task<ClubeResumo[]> tarefa = new Task<>() {
            @Override
            protected ClubeResumo[] call() {
                return Api.meusClubes();
            }
        };
        tarefa.setOnSucceeded(evento ->
                preencher(meusBox, tarefa.getValue(), "Voce ainda nao participa de nenhum clube."));
        tarefa.setOnFailed(evento -> App.erro(tarefa.getException()));
        new Thread(tarefa).start();
    }

    // Todos os clubes para explorar
    private void carregarTodos() {
        Task<ClubeResumo[]> tarefa = new Task<>() {
            @Override
            protected ClubeResumo[] call() {
                return Api.listarClubes();
            }
        };
        tarefa.setOnSucceeded(evento ->
                preencher(explorarBox, tarefa.getValue(), "Nenhum clube encontrado."));
        tarefa.setOnFailed(evento -> App.erro(tarefa.getException()));
        new Thread(tarefa).start();
    }

    private void preencher(VBox caixa, ClubeResumo[] clubes, String vazio) {
        caixa.getChildren().clear();
        if (clubes.length == 0) {
            caixa.getChildren().add(new Label(vazio));
            return;
        }
        for (ClubeResumo clube : clubes) {
            caixa.getChildren().add(criarCard(clube));
        }
    }

    private VBox criarCard(ClubeResumo c) {
        Label nome = new Label(c.nome());
        nome.setStyle("-fx-font-weight: bold;");
        Label desc = new Label(c.descricao());
        desc.setWrapText(true);
        Label info = new Label(c.quantidadeMembros() + " membros - " + c.tipoAcesso());
        Button abrir = new Button("Abrir");
        abrir.setOnAction(evento -> {
            // guarda o id do clube para a tela de detalhe ler
            App.parametro = c.id();
            App.mostrarConteudo("ClubeDetalhe");
        });
        VBox card = new VBox(6, nome, desc, info, abrir);
        card.getStyleClass().add("card");
        return card;
    }

    @FXML
    private void onCriarClube() {
        App.mostrarConteudo("CriarClube");
    }
}
