package com.henrique.ifconecta.desktop.controller;

import com.henrique.ifconecta.desktop.Api;
import com.henrique.ifconecta.desktop.App;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AdminController {

    // Campos do convite de professor
    @FXML private TextField profNome;
    @FXML private TextField profEmail;
    @FXML private TextField profSiape;

    // Campos do convite de institucional
    @FXML private TextField instNome;
    @FXML private TextField instEmail;
    @FXML private TextField instSetor;
    @FXML private TextField instCargo;

    @FXML
    private void onConvidarProfessor() {
        String nome = profNome.getText().trim();
        String email = profEmail.getText().trim();
        String siape = profSiape.getText().trim();
        // Todos os campos sao obrigatorios
        if (nome.isEmpty() || email.isEmpty() || siape.isEmpty()) {
            App.avisar("Preencha todos os campos do professor.");
            return;
        }
        // Chamada de rede roda em thread de fundo para nao travar a tela
        Task<Void> tarefa = new Task<>() {
            @Override
            protected Void call() {
                Api.convidarProfessor(nome, email, siape);
                return null;
            }
        };
        tarefa.setOnSucceeded(evento -> {
            App.avisar("Convite enviado!");
            // Limpa os campos para um novo convite
            profNome.setText("");
            profEmail.setText("");
            profSiape.setText("");
        });
        tarefa.setOnFailed(evento -> App.erro(tarefa.getException()));
        new Thread(tarefa).start();
    }

    @FXML
    private void onConvidarInstitucional() {
        String nome = instNome.getText().trim();
        String email = instEmail.getText().trim();
        String setor = instSetor.getText().trim();
        String cargo = instCargo.getText().trim();
        // Todos os campos sao obrigatorios
        if (nome.isEmpty() || email.isEmpty() || setor.isEmpty() || cargo.isEmpty()) {
            App.avisar("Preencha todos os campos do institucional.");
            return;
        }
        // Chamada de rede roda em thread de fundo para nao travar a tela
        Task<Void> tarefa = new Task<>() {
            @Override
            protected Void call() {
                Api.convidarInstitucional(nome, email, setor, cargo);
                return null;
            }
        };
        tarefa.setOnSucceeded(evento -> {
            App.avisar("Convite enviado!");
            // Limpa os campos para um novo convite
            instNome.setText("");
            instEmail.setText("");
            instSetor.setText("");
            instCargo.setText("");
        });
        tarefa.setOnFailed(evento -> App.erro(tarefa.getException()));
        new Thread(tarefa).start();
    }
}
