package com.henrique.ifconecta.desktop.controller;

import com.henrique.ifconecta.desktop.Api;
import com.henrique.ifconecta.desktop.App;
import com.henrique.ifconecta.desktop.Sessao;
import com.henrique.ifconecta.desktop.model.MeuPerfil;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Tela de login. Pega o email e a senha, chama a Api e, se der certo,
 * abre a area logada.
 */
public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField senhaField;
    @FXML private Button entrarBtn;

    @FXML
    private void onEntrar() {
        String email = emailField.getText().trim();
        String senha = senhaField.getText();

        if (email.isEmpty() || senha.isEmpty()) {
            App.avisar("Preencha o email e a senha.");
            return;
        }

        // A conversa com o servidor roda numa thread de fundo (Task) para a
        // janela nao travar enquanto espera a resposta.
        entrarBtn.setDisable(true);

        Task<MeuPerfil> tarefa = new Task<>() {
            @Override
            protected MeuPerfil call() {
                String token = Api.login(email, senha);
                Sessao.token = token;
                MeuPerfil perfil = Api.meuPerfil();
                Sessao.usuario = perfil;
                return perfil;
            }
        };

        // Quando termina com sucesso, abre a area logada.
        tarefa.setOnSucceeded(evento -> App.abrirAreaLogada());

        // Se der erro, mostra a mensagem e libera o botao de novo.
        tarefa.setOnFailed(evento -> {
            Sessao.token = null;
            entrarBtn.setDisable(false);
            App.erro(tarefa.getException());
        });

        new Thread(tarefa).start();
    }

    @FXML
    private void onCadastrar() {
        App.abrirTelaCheia("Cadastro");
    }
}
