package com.henrique.ifconecta.desktop.controller;

import com.henrique.ifconecta.desktop.Api;
import com.henrique.ifconecta.desktop.App;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CriarClubeController {

    @FXML private TextField nomeField;
    @FXML private TextArea descricaoArea;
    @FXML private ComboBox<String> tipoCombo;

    @FXML
    public void initialize() {
        // Opcoes amigaveis; convertemos para o valor da API na hora de criar.
        tipoCombo.getItems().addAll("Publico", "Privado");
        tipoCombo.getSelectionModel().selectFirst();
    }

    @FXML
    private void onCriar() {
        String nome = nomeField.getText().trim();
        String descricao = descricaoArea.getText().trim();
        if (nome.isEmpty() || descricao.isEmpty()) {
            App.avisar("Preencha nome e descricao.");
            return;
        }
        // A API espera "PUBLICO" ou "PRIVADO".
        String tipo = "Publico".equals(tipoCombo.getValue()) ? "PUBLICO" : "PRIVADO";
        Task<Void> tarefa = new Task<>() {
            @Override
            protected Void call() {
                Api.criarClube(nome, descricao, tipo);
                return null;
            }
        };
        tarefa.setOnSucceeded(evento -> App.mostrarConteudo("Clubes"));
        tarefa.setOnFailed(evento -> App.erro(tarefa.getException()));
        new Thread(tarefa).start();
    }

    @FXML
    private void onCancelar() {
        App.mostrarConteudo("Clubes");
    }
}
