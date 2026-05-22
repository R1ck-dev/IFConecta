package com.henrique.ifconecta.desktop.controller;

import com.henrique.ifconecta.desktop.core.AsyncRunner;
import com.henrique.ifconecta.desktop.core.http.ApiException;
import com.henrique.ifconecta.desktop.model.Comentario;
import com.henrique.ifconecta.desktop.model.PostDetalhe;
import com.henrique.ifconecta.desktop.service.PostService;
import com.henrique.ifconecta.desktop.ui.Avatar;
import com.henrique.ifconecta.desktop.ui.Format;
import com.henrique.ifconecta.desktop.ui.Modal;
import com.henrique.ifconecta.desktop.ui.Toast;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Modal de detalhe do post — exibe o post completo, a lista de comentários
 * e permite comentar. Espelha a página de detalhe do post do front web.
 */
public class PostDetalheController {

    @FXML private VBox raiz;
    @FXML private StackPane loadingBox;
    @FXML private VBox conteudoBox;
    @FXML private HBox cabecalhoBox;
    @FXML private Label corpoLabel;
    @FXML private Label upvotesLabel;
    @FXML private ScrollPane comentariosScroll;
    @FXML private VBox comentariosBox;
    @FXML private HBox novoComentarioBox;
    @FXML private TextField novoComentarioField;
    @FXML private Button comentarBtn;

    private String postId;

    /**
     * Carrega (ou recarrega) o detalhe do post indicado.
     *
     * @param postId id do post a exibir
     */
    public void carregar(String postId) {
        this.postId = postId;
        mostrarLoading(true);
        AsyncRunner.run(
                () -> PostService.detalhe(postId),
                detalhe -> {
                    mostrarLoading(false);
                    renderizar(detalhe);
                },
                erro -> {
                    mostrarLoading(false);
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível carregar o post", mensagem(erro));
                    }
                });
    }

    // ───────── Render ─────────

    private void renderizar(PostDetalhe detalhe) {
        boolean anonimo = detalhe.anonimo()
                || detalhe.autorNome() == null || detalhe.autorNome().isBlank();
        String nomeAutor = anonimo ? "Anônimo" : detalhe.autorNome();

        VBox autor = new VBox(2);
        Label nome = new Label(nomeAutor);
        nome.getStyleClass().add("post-author-name");
        Label tempo = new Label(Format.timeAgo(detalhe.dataCriacao()));
        tempo.getStyleClass().add("post-author-meta");
        autor.getChildren().addAll(nome, tempo);

        cabecalhoBox.getChildren().setAll(
                new Avatar(anonimo ? "" : detalhe.autorNome(), 40), autor);

        corpoLabel.setText(detalhe.conteudo());
        upvotesLabel.setText(detalhe.qtdUpvotes()
                + (detalhe.qtdUpvotes() == 1 ? " upvote" : " upvotes"));

        renderizarComentarios(detalhe);

        conteudoBox.setManaged(true);
        conteudoBox.setVisible(true);
        novoComentarioBox.setManaged(true);
        novoComentarioBox.setVisible(true);
    }

    private void renderizarComentarios(PostDetalhe detalhe) {
        comentariosBox.getChildren().clear();
        if (detalhe.comentarios() == null || detalhe.comentarios().isEmpty()) {
            Label vazio = new Label("Seja o primeiro a comentar");
            vazio.getStyleClass().add("text-muted");
            comentariosBox.getChildren().add(vazio);
            return;
        }
        for (Comentario comentario : detalhe.comentarios()) {
            comentariosBox.getChildren().add(cartaoComentario(comentario));
        }
    }

    private VBox cartaoComentario(Comentario comentario) {
        VBox caixa = new VBox();
        caixa.getStyleClass().add("comment");

        HBox topo = new HBox(8);
        topo.setAlignment(Pos.CENTER_LEFT);
        Label autor = new Label(comentario.autorNome() == null || comentario.autorNome().isBlank()
                ? "Anônimo" : comentario.autorNome());
        autor.getStyleClass().add("comment-author");
        Label meta = new Label(Format.timeAgo(comentario.dataCriacao()));
        meta.getStyleClass().add("comment-meta");
        topo.getChildren().addAll(autor, meta);

        Label corpo = new Label(comentario.conteudo());
        corpo.getStyleClass().add("comment-body");
        corpo.setWrapText(true);

        caixa.getChildren().addAll(topo, corpo);
        return caixa;
    }

    // ───────── Ações ─────────

    @FXML
    private void onComentar() {
        String texto = novoComentarioField.getText() == null
                ? "" : novoComentarioField.getText().trim();
        if (texto.isEmpty()) {
            Toast.error("Comentário vazio", "Escreva algo antes de comentar.");
            return;
        }
        comentarBtn.setDisable(true);
        AsyncRunner.runVoid(
                () -> PostService.comentar(postId, texto),
                () -> {
                    comentarBtn.setDisable(false);
                    novoComentarioField.clear();
                    carregar(postId);
                },
                erro -> {
                    comentarBtn.setDisable(false);
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível comentar", mensagem(erro));
                    }
                });
    }

    @FXML
    private void onFechar() {
        Modal.fechar(raiz);
    }

    // ───────── Auxiliares ─────────

    private void mostrarLoading(boolean visivel) {
        loadingBox.setManaged(visivel);
        loadingBox.setVisible(visivel);
    }

    private static String mensagem(Throwable erro) {
        if (erro instanceof ApiException api) {
            return api.getMessage();
        }
        return "Tente novamente em instantes.";
    }
}
