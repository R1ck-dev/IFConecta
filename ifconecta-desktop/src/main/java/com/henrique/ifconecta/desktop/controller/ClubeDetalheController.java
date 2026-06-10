package com.henrique.ifconecta.desktop.controller;

import com.henrique.ifconecta.desktop.Api;
import com.henrique.ifconecta.desktop.App;
import com.henrique.ifconecta.desktop.model.ClubeDetalhe;
import com.henrique.ifconecta.desktop.model.MembroClube;
import com.henrique.ifconecta.desktop.model.PostResumo;
import com.henrique.ifconecta.desktop.model.SolicitacaoMembro;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Tela de detalhe de um clube. Recebe o id do clube via App.parametro.
 * Tem 3 abas: Posts, Membros e Solicitacoes (essa ultima so o lider ve).
 */
public class ClubeDetalheController {

    @FXML private VBox cabecalhoBox;
    @FXML private TabPane abas;
    @FXML private VBox postsBox;
    @FXML private VBox membrosBox;
    @FXML private Tab solicitacoesTab;
    @FXML private VBox solicitacoesBox;

    // Guardamos o id e o clube carregado para usar nas varias tarefas.
    private String clubeId;
    private ClubeDetalhe clube;

    @FXML
    public void initialize() {
        // O clube que devemos abrir foi avisado pela tela anterior.
        clubeId = App.parametro;
        carregarClube();
    }

    private void carregarClube() {
        Task<ClubeDetalhe> tarefa = new Task<>() {
            @Override
            protected ClubeDetalhe call() {
                return Api.detalheClube(clubeId);
            }
        };
        tarefa.setOnSucceeded(evento -> {
            clube = tarefa.getValue();
            montarCabecalho();
            carregarPosts();
            carregarMembros();
            // A aba de solicitacoes so faz sentido para o lider.
            if (clube.souLider()) {
                carregarSolicitacoes();
            } else {
                abas.getTabs().remove(solicitacoesTab);
            }
        });
        tarefa.setOnFailed(evento -> App.erro(tarefa.getException()));
        new Thread(tarefa).start();
    }

    private void montarCabecalho() {
        cabecalhoBox.getChildren().clear();

        Label nome = new Label(clube.nome());
        nome.getStyleClass().add("titulo");

        Label desc = new Label(clube.descricao());
        desc.setWrapText(true);

        Label info = new Label(clube.quantidadeMembros() + " membros - "
                + clube.tipoAcesso() + " - Lider: " + clube.liderNome());

        cabecalhoBox.getChildren().addAll(nome, desc, info);

        // Mostra a situacao do usuario nesse clube (ou um botao para entrar).
        if (clube.souLider()) {
            cabecalhoBox.getChildren().add(new Label("Voce e o lider deste clube."));
        } else if (clube.souMembro()) {
            cabecalhoBox.getChildren().add(new Label("Voce e membro deste clube."));
        } else if (clube.temSolicitacaoPendente()) {
            Button b = new Button("Solicitacao enviada");
            b.setDisable(true);
            cabecalhoBox.getChildren().add(b);
        } else {
            Button b = new Button("Solicitar entrada");
            b.setOnAction(e -> solicitar());
            cabecalhoBox.getChildren().add(b);
        }
    }

    private void solicitar() {
        Task<Void> tarefa = new Task<>() {
            @Override
            protected Void call() {
                Api.solicitarEntrada(clubeId);
                return null;
            }
        };
        // Recarrega o clube para o cabecalho mostrar a nova situacao.
        tarefa.setOnSucceeded(evento -> carregarClube());
        tarefa.setOnFailed(evento -> App.erro(tarefa.getException()));
        new Thread(tarefa).start();
    }

    private void carregarPosts() {
        Task<PostResumo[]> tarefa = new Task<>() {
            @Override
            protected PostResumo[] call() {
                return Api.postsDoClube(clubeId);
            }
        };
        tarefa.setOnSucceeded(evento -> {
            postsBox.getChildren().clear();
            Button novo = new Button("Novo post");
            novo.setOnAction(e -> App.mostrarConteudo("CriarPost"));
            postsBox.getChildren().add(novo);
            for (PostResumo p : tarefa.getValue()) {
                postsBox.getChildren().add(criarCardPost(p));
            }
        });
        tarefa.setOnFailed(evento -> App.erro(tarefa.getException()));
        new Thread(tarefa).start();
    }

    private VBox criarCardPost(PostResumo p) {
        Label autor = new Label(p.autorNome());
        autor.setStyle("-fx-font-weight: bold;");

        Label texto = new Label(p.conteudo());
        texto.setWrapText(true);

        Button curtir = new Button("Curtir (" + p.qtdUpvotes() + ")");
        curtir.setOnAction(e -> upvote(p));

        Button coment = new Button("Comentarios (" + p.qtdComentarios() + ")");
        coment.setOnAction(e -> {
            App.parametro = p.id();
            App.mostrarConteudo("PostDetalhe");
        });

        HBox acoes = new HBox(8, curtir, coment);
        VBox card = new VBox(6, autor, texto, acoes);
        card.getStyleClass().add("card");
        return card;
    }

    private void upvote(PostResumo p) {
        Task<Void> tarefa = new Task<>() {
            @Override
            protected Void call() {
                Api.upvote(p.id());
                return null;
            }
        };
        tarefa.setOnSucceeded(evento -> carregarPosts());
        tarefa.setOnFailed(evento -> App.erro(tarefa.getException()));
        new Thread(tarefa).start();
    }

    private void carregarMembros() {
        Task<MembroClube[]> tarefa = new Task<>() {
            @Override
            protected MembroClube[] call() {
                return Api.membros(clubeId);
            }
        };
        tarefa.setOnSucceeded(evento -> {
            membrosBox.getChildren().clear();
            for (MembroClube m : tarefa.getValue()) {
                Label nome = new Label(m.nome());
                nome.setStyle("-fx-font-weight: bold;");
                Label papel = new Label(m.isLider() ? "Lider" : "Membro");
                VBox card = new VBox(4, nome, papel);
                card.getStyleClass().add("card");
                membrosBox.getChildren().add(card);
            }
        });
        tarefa.setOnFailed(evento -> App.erro(tarefa.getException()));
        new Thread(tarefa).start();
    }

    private void carregarSolicitacoes() {
        Task<SolicitacaoMembro[]> tarefa = new Task<>() {
            @Override
            protected SolicitacaoMembro[] call() {
                return Api.solicitacoes(clubeId);
            }
        };
        tarefa.setOnSucceeded(evento -> {
            solicitacoesBox.getChildren().clear();
            SolicitacaoMembro[] lista = tarefa.getValue();
            if (lista.length == 0) {
                solicitacoesBox.getChildren().add(new Label("Nenhuma solicitacao pendente."));
            } else {
                for (SolicitacaoMembro s : lista) {
                    Label nome = new Label(s.usuarioNome());
                    Button aprovar = new Button("Aprovar");
                    aprovar.setOnAction(e -> avaliar(s, true));
                    Button recusar = new Button("Recusar");
                    recusar.setOnAction(e -> avaliar(s, false));
                    HBox linha = new HBox(8, nome, aprovar, recusar);
                    linha.getStyleClass().add("card");
                    solicitacoesBox.getChildren().add(linha);
                }
            }
        });
        tarefa.setOnFailed(evento -> App.erro(tarefa.getException()));
        new Thread(tarefa).start();
    }

    private void avaliar(SolicitacaoMembro s, boolean aprovado) {
        Task<Void> tarefa = new Task<>() {
            @Override
            protected Void call() {
                Api.avaliarMembro(clubeId, s.usuarioId(), aprovado);
                return null;
            }
        };
        // Recarrega tudo: a lista de membros e de solicitacoes muda.
        tarefa.setOnSucceeded(evento -> carregarClube());
        tarefa.setOnFailed(evento -> App.erro(tarefa.getException()));
        new Thread(tarefa).start();
    }

    @FXML
    private void onVoltar() {
        App.mostrarConteudo("Clubes");
    }
}
