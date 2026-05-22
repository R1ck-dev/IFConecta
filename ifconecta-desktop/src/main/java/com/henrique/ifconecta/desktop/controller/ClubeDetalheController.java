package com.henrique.ifconecta.desktop.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.henrique.ifconecta.desktop.core.AsyncRunner;
import com.henrique.ifconecta.desktop.core.RouteParam;
import com.henrique.ifconecta.desktop.core.Router;
import com.henrique.ifconecta.desktop.core.http.ApiException;
import com.henrique.ifconecta.desktop.model.ClubeDetalhe;
import com.henrique.ifconecta.desktop.model.MembroClube;
import com.henrique.ifconecta.desktop.model.PostResumo;
import com.henrique.ifconecta.desktop.model.SolicitacaoMembro;
import com.henrique.ifconecta.desktop.service.ClubeService;
import com.henrique.ifconecta.desktop.service.PostService;
import com.henrique.ifconecta.desktop.ui.Avatar;
import com.henrique.ifconecta.desktop.ui.Format;
import com.henrique.ifconecta.desktop.ui.Icons;
import com.henrique.ifconecta.desktop.ui.Modal;
import com.henrique.ifconecta.desktop.ui.PostCard;
import com.henrique.ifconecta.desktop.ui.Toast;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Detalhe de um clube — cabeçalho + abas de Posts, Membros e Solicitações. */
public class ClubeDetalheController implements RouteParam {

    private static final int TAMANHO_PAGINA = 10;

    private enum Aba { POSTS, MEMBROS, SOLICITACOES }

    @FXML private StackPane loadingBox;
    @FXML private VBox corpoBox;
    @FXML private Label nomeLabel;
    @FXML private Label descricaoLabel;
    @FXML private Label tipoChip;
    @FXML private Label membrosLabel;
    @FXML private Label liderLabel;
    @FXML private StackPane acaoBox;
    @FXML private Button abaPosts;
    @FXML private Button abaMembros;
    @FXML private Button abaSolicitacoes;
    @FXML private VBox conteudoAba;

    private String clubeId;
    private ClubeDetalhe clube;
    private Aba abaAtual = Aba.POSTS;

    private final List<PostResumo> posts = new ArrayList<>();
    private final Set<String> upvoteEmAndamento = new HashSet<>();
    private int paginaPosts;
    private int totalPaginasPosts;

    @Override
    public void aoNavegar(Object argumento) {
        clubeId = (String) argumento;
        carregarClube();
    }

    // ───────── Clube ─────────

    private void carregarClube() {
        mostrarLoading(true);
        corpoBox.setVisible(false);
        corpoBox.setManaged(false);

        AsyncRunner.run(
                () -> ClubeService.detalhe(clubeId),
                detalhe -> {
                    mostrarLoading(false);
                    corpoBox.setVisible(true);
                    corpoBox.setManaged(true);
                    clube = detalhe;
                    renderizarCabecalho();
                    selecionarAba(abaAtual);
                },
                erro -> {
                    mostrarLoading(false);
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível carregar o clube", mensagem(erro));
                    }
                });
    }

    void recarregar() {
        carregarClube();
    }

    private void renderizarCabecalho() {
        nomeLabel.setText(clube.nome());
        descricaoLabel.setText(clube.descricao() == null ? "" : clube.descricao());

        boolean priv = privado(clube.tipoAcesso());
        tipoChip.setText(priv ? "Privado" : "Público");
        tipoChip.getStyleClass().setAll("chip", priv ? "chip-muted" : "chip-info");

        membrosLabel.setText(clube.quantidadeMembros() + " membros");
        liderLabel.setText("Líder: " + (clube.liderNome() == null ? "—" : clube.liderNome()));

        acaoBox.getChildren().setAll(botaoAcao());

        boolean lider = clube.souLider();
        abaSolicitacoes.setVisible(lider);
        abaSolicitacoes.setManaged(lider);
        if (!lider && abaAtual == Aba.SOLICITACOES) {
            abaAtual = Aba.POSTS;
        }
    }

    private javafx.scene.Node botaoAcao() {
        if (clube.souLider()) {
            Button comunicado = new Button("Comunicado");
            comunicado.getStyleClass().addAll("btn", "btn-sm", "btn-soft");
            comunicado.setGraphic(Icons.of("fth-send", 14));
            comunicado.setOnAction(e -> Modal.abrir("comunicado", "Enviar comunicado",
                    (ComunicadoController c) -> c.fixarClube(clube.id())));
            return comunicado;
        }
        if (clube.souMembro()) {
            Label chip = new Label("Você é membro");
            chip.getStyleClass().addAll("chip", "chip-success");
            return chip;
        }
        if (clube.temSolicitacaoPendente()) {
            Button enviada = new Button("Solicitação enviada");
            enviada.getStyleClass().addAll("btn", "btn-sm", "btn-outline");
            enviada.setDisable(true);
            return enviada;
        }
        Button solicitar = new Button("Solicitar entrada");
        solicitar.getStyleClass().addAll("btn", "btn-sm");
        solicitar.setOnAction(e -> solicitarEntrada(solicitar));
        return solicitar;
    }

    private void solicitarEntrada(Button origem) {
        origem.setDisable(true);
        AsyncRunner.runVoid(
                () -> ClubeService.solicitarEntrada(clubeId),
                () -> {
                    Toast.success("Solicitação enviada!");
                    recarregar();
                },
                erro -> {
                    origem.setDisable(false);
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível solicitar entrada", mensagem(erro));
                    }
                });
    }

    // ───────── Abas ─────────

    @FXML
    private void onAbaPosts() {
        selecionarAba(Aba.POSTS);
    }

    @FXML
    private void onAbaMembros() {
        selecionarAba(Aba.MEMBROS);
    }

    @FXML
    private void onAbaSolicitacoes() {
        selecionarAba(Aba.SOLICITACOES);
    }

    private void selecionarAba(Aba aba) {
        abaAtual = aba;
        marcarAbaAtiva(abaPosts, aba == Aba.POSTS);
        marcarAbaAtiva(abaMembros, aba == Aba.MEMBROS);
        marcarAbaAtiva(abaSolicitacoes, aba == Aba.SOLICITACOES);
        switch (aba) {
            case POSTS -> carregarPosts(0);
            case MEMBROS -> carregarMembros();
            case SOLICITACOES -> carregarSolicitacoes();
        }
    }

    private static void marcarAbaAtiva(Button aba, boolean ativa) {
        aba.getStyleClass().remove("active");
        if (ativa) {
            aba.getStyleClass().add("active");
        }
    }

    private void mostrarCarregandoAba() {
        ProgressFiller filler = new ProgressFiller();
        conteudoAba.getChildren().setAll(filler);
    }

    /** Pequeno indicador centralizado de carregamento de aba. */
    private static final class ProgressFiller extends StackPane {
        ProgressFiller() {
            javafx.scene.control.ProgressIndicator pi = new javafx.scene.control.ProgressIndicator();
            pi.setPrefSize(36, 36);
            setAlignment(Pos.CENTER);
            setPadding(new javafx.geometry.Insets(32));
            getChildren().add(pi);
        }
    }

    // ───────── Aba Posts ─────────

    private void carregarPosts(int pagina) {
        boolean primeira = pagina == 0;
        if (primeira) {
            mostrarCarregandoAba();
        }
        AsyncRunner.run(
                () -> ClubeService.posts(clubeId, pagina, TAMANHO_PAGINA),
                page -> {
                    if (abaAtual != Aba.POSTS) {
                        return;
                    }
                    if (primeira) {
                        posts.clear();
                    }
                    posts.addAll(page.itens());
                    paginaPosts = page.paginaAtual();
                    totalPaginasPosts = page.totalPaginas();
                    renderizarPosts();
                },
                erro -> {
                    if (abaAtual != Aba.POSTS) {
                        return;
                    }
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível carregar os posts", mensagem(erro));
                    }
                    renderizarPosts();
                });
    }

    void recarregarPosts() {
        carregarPosts(0);
    }

    private void renderizarPosts() {
        conteudoAba.getChildren().clear();

        Button novoPost = new Button("Novo post");
        novoPost.getStyleClass().addAll("btn", "btn-sm");
        novoPost.setGraphic(Icons.of("fth-plus", 14));
        novoPost.setOnAction(e -> Modal.abrir("criar-post", "Novo post",
                (CriarPostController c) -> c.configurar(clubeId, this::recarregarPosts)));
        HBox topo = new HBox(novoPost);
        topo.setAlignment(Pos.CENTER_LEFT);
        conteudoAba.getChildren().add(topo);

        if (posts.isEmpty()) {
            conteudoAba.getChildren().add(estadoVazio(
                    "fth-message-square", "Nenhum post ainda",
                    "Seja o primeiro a publicar algo neste clube."));
            return;
        }

        VBox lista = new VBox(14);
        for (PostResumo post : posts) {
            lista.getChildren().add(PostCard.criar(post, this::darUpvote,
                    p -> Modal.abrir("post-detalhe", "Post",
                            (PostDetalheController c) -> c.carregar(p.id()))));
        }
        conteudoAba.getChildren().add(lista);

        if (paginaPosts + 1 < totalPaginasPosts) {
            Button carregarMais = new Button("Carregar mais");
            carregarMais.getStyleClass().addAll("btn", "btn-outline");
            carregarMais.setOnAction(e -> carregarPosts(paginaPosts + 1));
            StackPane wrap = new StackPane(carregarMais);
            wrap.setAlignment(Pos.CENTER);
            conteudoAba.getChildren().add(wrap);
        }
    }

    private void darUpvote(PostResumo post) {
        if (upvoteEmAndamento.contains(post.id())) {
            return;
        }
        int indice = indiceDe(post.id());
        if (indice < 0) {
            return;
        }
        upvoteEmAndamento.add(post.id());
        posts.set(indice, post.comUpvoteAlternado());
        renderizarPosts();

        AsyncRunner.runVoid(
                () -> PostService.upvote(post.id()),
                () -> upvoteEmAndamento.remove(post.id()),
                erro -> {
                    upvoteEmAndamento.remove(post.id());
                    int i = indiceDe(post.id());
                    if (i >= 0) {
                        posts.set(i, post);
                        renderizarPosts();
                    }
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível registrar o upvote", mensagem(erro));
                    }
                });
    }

    private int indiceDe(String postId) {
        for (int i = 0; i < posts.size(); i++) {
            if (posts.get(i).id().equals(postId)) {
                return i;
            }
        }
        return -1;
    }

    // ───────── Aba Membros ─────────

    private void carregarMembros() {
        mostrarCarregandoAba();
        AsyncRunner.run(
                () -> ClubeService.membros(clubeId),
                membros -> {
                    if (abaAtual == Aba.MEMBROS) {
                        renderizarMembros(membros);
                    }
                },
                erro -> {
                    if (abaAtual != Aba.MEMBROS) {
                        return;
                    }
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível carregar os membros", mensagem(erro));
                    }
                    renderizarMembros(List.of());
                });
    }

    private void renderizarMembros(List<MembroClube> membros) {
        conteudoAba.getChildren().clear();
        if (membros == null || membros.isEmpty()) {
            conteudoAba.getChildren().add(estadoVazio(
                    "fth-users", "Nenhum membro ainda",
                    "Este clube ainda não tem membros."));
            return;
        }
        VBox lista = new VBox(10);
        for (MembroClube membro : membros) {
            lista.getChildren().add(linhaMembro(membro));
        }
        conteudoAba.getChildren().add(lista);
    }

    private HBox linhaMembro(MembroClube membro) {
        HBox linha = new HBox(12);
        linha.getStyleClass().add("list-card");
        linha.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(2);
        Label nome = new Label(membro.nome());
        nome.getStyleClass().add("item-title");
        Label tipo = new Label(membro.tipo() == null ? "" : membro.tipo());
        tipo.getStyleClass().add("item-meta");
        info.getChildren().addAll(nome, tipo);
        HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);

        Label papel = new Label(membro.isLider() ? "Líder" : "Membro");
        papel.getStyleClass().addAll("chip", membro.isLider() ? "chip" : "chip-muted");

        linha.getChildren().addAll(new Avatar(membro.nome(), 40), info, papel);
        return linha;
    }

    // ───────── Aba Solicitações ─────────

    private void carregarSolicitacoes() {
        mostrarCarregandoAba();
        AsyncRunner.run(
                () -> ClubeService.solicitacoes(clubeId),
                solicitacoes -> {
                    if (abaAtual == Aba.SOLICITACOES) {
                        renderizarSolicitacoes(solicitacoes);
                    }
                },
                erro -> {
                    if (abaAtual != Aba.SOLICITACOES) {
                        return;
                    }
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível carregar as solicitações", mensagem(erro));
                    }
                    renderizarSolicitacoes(List.of());
                });
    }

    private void renderizarSolicitacoes(List<SolicitacaoMembro> solicitacoes) {
        conteudoAba.getChildren().clear();
        if (solicitacoes == null || solicitacoes.isEmpty()) {
            conteudoAba.getChildren().add(estadoVazio(
                    "fth-user-check", "Nenhuma solicitação pendente",
                    "Quando alguém pedir para entrar, a solicitação aparece aqui."));
            return;
        }
        VBox lista = new VBox(10);
        for (SolicitacaoMembro solicitacao : solicitacoes) {
            lista.getChildren().add(linhaSolicitacao(solicitacao));
        }
        conteudoAba.getChildren().add(lista);
    }

    private HBox linhaSolicitacao(SolicitacaoMembro solicitacao) {
        HBox linha = new HBox(12);
        linha.getStyleClass().add("list-card");
        linha.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(2);
        Label nome = new Label(solicitacao.usuarioNome());
        nome.getStyleClass().add("item-title");
        Label quando = new Label("Solicitado " + Format.timeAgo(solicitacao.dataSolicitacao()));
        quando.getStyleClass().add("item-meta");
        info.getChildren().addAll(nome, quando);
        HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);

        Button aprovar = new Button("Aprovar");
        aprovar.getStyleClass().addAll("btn", "btn-sm");

        Button recusar = new Button("Recusar");
        recusar.getStyleClass().addAll("btn", "btn-sm", "btn-outline");

        aprovar.setOnAction(e -> avaliar(solicitacao, true, aprovar, recusar));
        recusar.setOnAction(e -> avaliar(solicitacao, false, aprovar, recusar));

        linha.getChildren().addAll(new Avatar(solicitacao.usuarioNome(), 40), info, aprovar, recusar);
        return linha;
    }

    private void avaliar(SolicitacaoMembro solicitacao, boolean aprovado, Button aprovar, Button recusar) {
        aprovar.setDisable(true);
        recusar.setDisable(true);
        AsyncRunner.runVoid(
                () -> ClubeService.avaliarMembro(clubeId, solicitacao.usuarioId(), aprovado),
                () -> {
                    Toast.success(aprovado ? "Membro aprovado!" : "Solicitação recusada.");
                    carregarSolicitacoes();
                },
                erro -> {
                    aprovar.setDisable(false);
                    recusar.setDisable(false);
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível avaliar a solicitação", mensagem(erro));
                    }
                });
    }

    // ───────── Navegação ─────────

    @FXML
    private void onVoltar() {
        Router.get().navigate("clubes");
    }

    // ───────── Auxiliares ─────────

    private VBox estadoVazio(String icone, String titulo, String mensagem) {
        VBox caixa = new VBox(8);
        caixa.getStyleClass().add("empty");

        StackPane iconeBox = new StackPane(Icons.of(icone, 24));
        iconeBox.getStyleClass().add("empty-icon");

        Label tituloLabel = new Label(titulo);
        tituloLabel.getStyleClass().add("empty-title");

        Label msgLabel = new Label(mensagem);
        msgLabel.getStyleClass().add("empty-msg");
        msgLabel.setWrapText(true);

        caixa.getChildren().addAll(iconeBox, tituloLabel, msgLabel);
        return caixa;
    }

    private void mostrarLoading(boolean visivel) {
        loadingBox.setVisible(visivel);
        loadingBox.setManaged(visivel);
    }

    private static boolean privado(String tipoAcesso) {
        return "PRIVADO".equalsIgnoreCase(tipoAcesso);
    }

    private static String mensagem(Throwable erro) {
        if (erro instanceof ApiException api) {
            return api.getMessage();
        }
        return "Tente novamente em instantes.";
    }
}
