package com.henrique.ifconecta.desktop.controller;

import com.henrique.ifconecta.desktop.Api;
import com.henrique.ifconecta.desktop.App;
import com.henrique.ifconecta.desktop.model.ClubeResumo;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ComunicadoController {

    @FXML private TextField tituloField;
    @FXML private TextArea mensagemArea;
    @FXML private ComboBox<String> publicoCombo;
    @FXML private ComboBox<String> clubeCombo;

    // guardo os clubes para pegar o id pelo indice escolhido no combo
    private ClubeResumo[] meusClubes;

    @FXML
    public void initialize() {
        // opcoes de publico; Geral ja vem selecionada
        publicoCombo.getItems().addAll("Geral", "Clube");
        publicoCombo.getSelectionModel().selectFirst();
        carregarClubes();
    }

    private void carregarClubes() {
        // busca na rede entao roda numa Task de fundo
        Task<ClubeResumo[]> tarefa = new Task<>() {
            @Override
            protected ClubeResumo[] call() {
                return Api.meusClubes();
            }
        };
        tarefa.setOnSucceeded(evento -> {
            meusClubes = tarefa.getValue();
            for (ClubeResumo c : meusClubes) {
                clubeCombo.getItems().add(c.nome());
            }
        });
        tarefa.setOnFailed(evento -> App.erro(tarefa.getException()));
        new Thread(tarefa).start();
    }

    @FXML
    private void onEnviar() {
        String titulo = tituloField.getText().trim();
        String mensagem = mensagemArea.getText().trim();
        if (titulo.isEmpty() || mensagem.isEmpty()) {
            App.avisar("Preencha titulo e mensagem.");
            return;
        }

        // descobre o alvo conforme o publico escolhido
        String tipoAlvo;
        String alvoId;
        if ("Geral".equals(publicoCombo.getValue())) {
            tipoAlvo = "GERAL";
            alvoId = null;
        } else {
            int i = clubeCombo.getSelectionModel().getSelectedIndex();
            if (i < 0) {
                App.avisar("Escolha um clube.");
                return;
            }
            tipoAlvo = "CLUBE";
            alvoId = meusClubes[i].id();
        }

        Task<Void> tarefa = new Task<>() {
            @Override
            protected Void call() {
                Api.enviarComunicado(titulo, mensagem, tipoAlvo, alvoId);
                return null;
            }
        };
        tarefa.setOnSucceeded(evento -> {
            App.avisar("Comunicado enviado!");
            App.mostrarConteudo("Timeline");
        });
        tarefa.setOnFailed(evento -> App.erro(tarefa.getException()));
        new Thread(tarefa).start();
    }

    @FXML
    private void onCancelar() {
        App.mostrarConteudo("Timeline");
    }
}
