package com.henrique.ifconecta.desktop.controller;

import com.henrique.ifconecta.desktop.App;
import com.henrique.ifconecta.desktop.Sessao;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controla a moldura da area logada: o cabecalho e o menu lateral.
 * Cada botao do menu pede ao App para mostrar a tela correspondente.
 */
public class AppShellController {

    @FXML private Label nomeUsuarioLabel;
    @FXML private Button comunicadoBtn;
    @FXML private Button adminBtn;

    @FXML
    public void initialize() {
        nomeUsuarioLabel.setText(Sessao.usuario.nome());

        // O botao "Comunicado" so aparece para professores e servidores.
        boolean podeComunicar = Sessao.usuario.podeComunicar();
        comunicadoBtn.setVisible(podeComunicar);
        comunicadoBtn.setManaged(podeComunicar);

        // O "Painel admin" so aparece para administradores.
        boolean admin = Sessao.usuario.isAdmin();
        adminBtn.setVisible(admin);
        adminBtn.setManaged(admin);
    }

    @FXML
    private void irTimeline() {
        App.mostrarConteudo("Timeline");
    }

    @FXML
    private void irClubes() {
        App.mostrarConteudo("Clubes");
    }

    @FXML
    private void irNotificacoes() {
        App.mostrarConteudo("Notificacoes");
    }

    @FXML
    private void irAdmin() {
        App.mostrarConteudo("Admin");
    }

    @FXML
    private void irComunicado() {
        App.mostrarConteudo("Comunicado");
    }

    @FXML
    private void sair() {
        Sessao.token = null;
        Sessao.usuario = null;
        App.abrirTelaCheia("Login");
    }
}
