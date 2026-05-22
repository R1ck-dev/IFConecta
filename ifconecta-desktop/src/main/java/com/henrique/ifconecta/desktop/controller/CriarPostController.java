package com.henrique.ifconecta.desktop.controller;

import java.util.List;

import com.henrique.ifconecta.desktop.core.AsyncRunner;
import com.henrique.ifconecta.desktop.core.http.ApiException;
import com.henrique.ifconecta.desktop.model.ClubeResumo;
import com.henrique.ifconecta.desktop.service.ClubeService;
import com.henrique.ifconecta.desktop.service.PostService;
import com.henrique.ifconecta.desktop.ui.Modal;
import com.henrique.ifconecta.desktop.ui.Toast;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * Modal de criação de post — porta o composer de TimelinePage para uma janela.
 * Permite escolher um clube (opcional) e postar de forma anônima.
 */
public class CriarPostController {

    @FXML private VBox raiz;
    @FXML private TextArea conteudoArea;
    @FXML private Label conteudoErro;
    @FXML private ComboBox<ClubeResumo> clubeCombo;
    @FXML private CheckBox anonimoCheck;
    @FXML private Button publicarBtn;

    private String clubeIdInicial;
    private Runnable aoCriar;

    @FXML
    private void initialize() {
        clubeCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(ClubeResumo clube) {
                return clube == null ? null : clube.nome();
            }

            @Override
            public ClubeResumo fromString(String texto) {
                return null;
            }
        });
        carregarClubes();
    }

    /**
     * Configura o modal antes de exibi-lo.
     *
     * @param clubeIdInicial se != null, pré-seleciona esse clube e desabilita a troca
     * @param aoCriar        callback disparado após a publicação bem-sucedida
     */
    public void configurar(String clubeIdInicial, Runnable aoCriar) {
        this.clubeIdInicial = clubeIdInicial;
        this.aoCriar = aoCriar;
        aplicarClubeInicial();
    }

    // ───────── Carregamento ─────────

    private void carregarClubes() {
        AsyncRunner.run(
                ClubeService::meusClubes,
                clubes -> {
                    clubeCombo.getItems().setAll(clubes);
                    aplicarClubeInicial();
                },
                erro -> {
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível carregar os clubes", mensagem(erro));
                    }
                });
    }

    private void aplicarClubeInicial() {
        if (clubeIdInicial == null) {
            return;
        }
        List<ClubeResumo> clubes = clubeCombo.getItems();
        for (ClubeResumo clube : clubes) {
            if (clube.id().equals(clubeIdInicial)) {
                clubeCombo.setValue(clube);
                clubeCombo.setDisable(true);
                return;
            }
        }
    }

    // ───────── Ações ─────────

    @FXML
    private void onCancelar() {
        Modal.fechar(raiz);
    }

    @FXML
    private void onPublicar() {
        String conteudo = conteudoArea.getText() == null ? "" : conteudoArea.getText().trim();
        if (conteudo.isEmpty()) {
            mostrarErro("Escreva algo antes de publicar.");
            return;
        }
        limparErro();

        ClubeResumo clube = clubeCombo.getValue();
        String clubeId = clube == null ? null : clube.id();
        boolean anonimo = anonimoCheck.isSelected();

        publicarBtn.setDisable(true);
        AsyncRunner.runVoid(
                () -> PostService.criar(conteudo, clubeId, anonimo),
                () -> {
                    if (aoCriar != null) {
                        aoCriar.run();
                    }
                    Toast.success("Post publicado!");
                    Modal.fechar(raiz);
                },
                erro -> {
                    publicarBtn.setDisable(false);
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível publicar o post", mensagem(erro));
                    }
                });
    }

    // ───────── Auxiliares ─────────

    private void mostrarErro(String texto) {
        conteudoErro.setText(texto);
        conteudoErro.setManaged(true);
        conteudoErro.setVisible(true);
        conteudoArea.getStyleClass().add("has-error");
    }

    private void limparErro() {
        conteudoErro.setManaged(false);
        conteudoErro.setVisible(false);
        conteudoArea.getStyleClass().remove("has-error");
    }

    private static String mensagem(Throwable erro) {
        if (erro instanceof ApiException api) {
            return api.getMessage();
        }
        return "Tente novamente em instantes.";
    }
}
