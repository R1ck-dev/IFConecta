package com.henrique.ifconecta.desktop.controller;

import com.henrique.ifconecta.desktop.Api;
import com.henrique.ifconecta.desktop.App;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CadastroController {

    @FXML private TextField nomeField;
    @FXML private TextField emailField;
    @FXML private PasswordField senhaField;
    @FXML private TextField prontuarioField;
    @FXML private Button cadastrarBtn;

    @FXML
    private void onCadastrar() {
        // Le os campos. Tiro os espacos das pontas (menos da senha, que vai como esta).
        String nome = nomeField.getText().trim();
        String email = emailField.getText().trim();
        String senha = senhaField.getText();
        String prontuario = prontuarioField.getText().trim();

        // Validacoes simples antes de chamar o servidor.
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || prontuario.isEmpty()) {
            App.avisar("Preencha todos os campos.");
            return;
        }
        if (senha.length() < 8) {
            App.avisar("A senha precisa ter ao menos 8 caracteres.");
            return;
        }

        // Evita clicar duas vezes enquanto o servidor responde.
        cadastrarBtn.setDisable(true);

        // A chamada de rede trava, entao vai numa Task (thread de fundo).
        Task<Void> tarefa = new Task<>() {
            @Override
            protected Void call() {
                Api.cadastrarAluno(nome, email, senha, prontuario);
                return null;
            }
        };
        tarefa.setOnSucceeded(evento -> {
            App.avisar("Conta criada! Verifique seu e-mail para ativar.");
            App.abrirTelaCheia("Login");
        });
        tarefa.setOnFailed(evento -> {
            cadastrarBtn.setDisable(false);
            App.erro(tarefa.getException());
        });
        new Thread(tarefa).start();
    }

    @FXML
    private void onVoltar() {
        App.abrirTelaCheia("Login");
    }
}
