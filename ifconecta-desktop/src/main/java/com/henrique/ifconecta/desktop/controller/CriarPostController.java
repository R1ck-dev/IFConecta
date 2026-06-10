package com.henrique.ifconecta.desktop.controller;

import com.henrique.ifconecta.desktop.Api;
import com.henrique.ifconecta.desktop.App;
import com.henrique.ifconecta.desktop.model.ClubeResumo;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class CriarPostController {

    @FXML private TextArea conteudoArea;
    @FXML private ComboBox<String> clubeCombo;
    @FXML private CheckBox anonimoCheck;

    // guardo os clubes para descobrir o id pelo indice escolhido
    private ClubeResumo[] meusClubes;

    @FXML
    public void initialize() {
        Task<ClubeResumo[]> tarefa = new Task<>() {
            @Override
            protected ClubeResumo[] call() {
                return Api.meusClubes();
            }
        };
        tarefa.setOnSucceeded(evento -> {
            meusClubes = tarefa.getValue();
            // primeiro item representa post sem clube
            clubeCombo.getItems().add("Nenhum (post geral)");
            for (ClubeResumo clube : meusClubes) {
                clubeCombo.getItems().add(clube.nome());
            }
            clubeCombo.getSelectionModel().selectFirst();
        });
        tarefa.setOnFailed(evento -> App.erro(tarefa.getException()));
        new Thread(tarefa).start();
    }

    @FXML
    private void onPublicar() {
        String conteudo = conteudoArea.getText().trim();
        if (conteudo.isEmpty()) {
            App.avisar("Escreva algo para publicar.");
            return;
        }
        // indice 0 e "Nenhum", entao clube fica null; senao pego o id pelo indice - 1
        int i = clubeCombo.getSelectionModel().getSelectedIndex();
        String clubeId = (i <= 0) ? null : meusClubes[i - 1].id();
        boolean anonimo = anonimoCheck.isSelected();

        Task<Void> tarefa = new Task<>() {
            @Override
            protected Void call() {
                Api.criarPost(conteudo, clubeId, anonimo);
                return null;
            }
        };
        tarefa.setOnSucceeded(evento -> App.mostrarConteudo("Timeline"));
        tarefa.setOnFailed(evento -> App.erro(tarefa.getException()));
        new Thread(tarefa).start();
    }

    @FXML
    private void onCancelar() {
        App.mostrarConteudo("Timeline");
    }
}
