package com.henrique.ifconecta.desktop;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.henrique.ifconecta.desktop.model.ClubeDetalhe;
import com.henrique.ifconecta.desktop.model.ClubeResumo;
import com.henrique.ifconecta.desktop.model.MembroClube;
import com.henrique.ifconecta.desktop.model.MeuPerfil;
import com.henrique.ifconecta.desktop.model.NotificacaoResumo;
import com.henrique.ifconecta.desktop.model.PostDetalhe;
import com.henrique.ifconecta.desktop.model.PostResumo;
import com.henrique.ifconecta.desktop.model.SolicitacaoMembro;
import com.henrique.ifconecta.desktop.model.TokenResponse;

/**
 * Conversa com o backend do IFConecta (servidor REST em http://localhost:8080).
 * Cada metodo monta um pedido HTTP, envia, e devolve a resposta ja convertida
 * em objetos (records da pasta model).
 *
 * ATENCAO: estes metodos ficam parados esperando o servidor responder. Por isso
 * devem ser chamados de dentro de uma Task (thread de fundo), nunca direto na
 * tela, senao a janela trava.
 */
public class Api {

    /** Endereco do servidor. Troque aqui se o backend estiver em outro lugar. */
    private static final String BASE = "http://localhost:8080/api";

    /** Objeto que faz os pedidos HTTP. */
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    /** Objeto que converte texto JSON em objetos Java (e vice-versa). */
    private static final ObjectMapper JSON = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    // ----- Login e perfil -----

    /** Faz login. Devolve o token; guarde-o em Sessao.token. */
    public static String login(String email, String senha) {
        HashMap<String, Object> corpo = new HashMap<>();
        corpo.put("email", email);
        corpo.put("password", senha);
        String resposta = enviar("POST", "/auth/login", corpo);
        TokenResponse token = (TokenResponse) lerObjeto(resposta, TokenResponse.class);
        return token.token();
    }

    /** Busca os dados do usuario que esta logado. */
    public static MeuPerfil meuPerfil() {
        String resposta = enviar("GET", "/usuarios/me", null);
        return (MeuPerfil) lerObjeto(resposta, MeuPerfil.class);
    }

    /** Cadastra um novo aluno. */
    public static void cadastrarAluno(String nome, String email, String senha, String prontuario) {
        HashMap<String, Object> corpo = new HashMap<>();
        corpo.put("nome", nome);
        corpo.put("email", email);
        corpo.put("password", senha);
        corpo.put("prontuario", prontuario);
        enviar("POST", "/usuarios/alunos", corpo);
    }

    // ----- Painel do administrador -----

    /** Convida um professor por e-mail (so admin). */
    public static void convidarProfessor(String nome, String emailAcad, String siape) {
        HashMap<String, Object> corpo = new HashMap<>();
        corpo.put("nome", nome);
        corpo.put("emailAcad", emailAcad);
        corpo.put("siape", siape);
        enviar("POST", "/admin/usuarios/professores/convidar", corpo);
    }

    /** Convida um servidor institucional por e-mail (so admin). */
    public static void convidarInstitucional(String nome, String emailAcad, String setor, String cargo) {
        HashMap<String, Object> corpo = new HashMap<>();
        corpo.put("nome", nome);
        corpo.put("emailAcad", emailAcad);
        corpo.put("setor", setor);
        corpo.put("cargo", cargo);
        enviar("POST", "/admin/usuarios/institucionais/convidar", corpo);
    }

    // ----- Clubes -----

    /** Lista todos os clubes do campus. */
    public static ClubeResumo[] listarClubes() {
        String resposta = enviar("GET", "/clubes?pagina=0&tamanho=100", null);
        return (ClubeResumo[]) lerItens(resposta, ClubeResumo[].class);
    }

    /** Lista os clubes dos quais o usuario logado e membro. */
    public static ClubeResumo[] meusClubes() {
        String resposta = enviar("GET", "/clubes/meus", null);
        return (ClubeResumo[]) lerObjeto(resposta, ClubeResumo[].class);
    }

    /** Detalhe de um clube. */
    public static ClubeDetalhe detalheClube(String clubeId) {
        String resposta = enviar("GET", "/clubes/" + clubeId, null);
        return (ClubeDetalhe) lerObjeto(resposta, ClubeDetalhe.class);
    }

    /** Membros aprovados de um clube. */
    public static MembroClube[] membros(String clubeId) {
        String resposta = enviar("GET", "/clubes/" + clubeId + "/membros", null);
        return (MembroClube[]) lerObjeto(resposta, MembroClube[].class);
    }

    /** Pedidos de entrada pendentes (so o lider ve). */
    public static SolicitacaoMembro[] solicitacoes(String clubeId) {
        String resposta = enviar("GET", "/clubes/" + clubeId + "/solicitacoes", null);
        return (SolicitacaoMembro[]) lerObjeto(resposta, SolicitacaoMembro[].class);
    }

    /** Posts publicados dentro de um clube. */
    public static PostResumo[] postsDoClube(String clubeId) {
        String resposta = enviar("GET", "/clubes/" + clubeId + "/posts?pagina=0&tamanho=100", null);
        return (PostResumo[]) lerItens(resposta, PostResumo[].class);
    }

    /** Cria um clube novo. tipoAcesso e "PUBLICO" ou "PRIVADO". */
    public static void criarClube(String nome, String descricao, String tipoAcesso) {
        HashMap<String, Object> corpo = new HashMap<>();
        corpo.put("nome", nome);
        corpo.put("descricao", descricao);
        corpo.put("tipoAcesso", tipoAcesso);
        enviar("POST", "/clubes", corpo);
    }

    /** Pede para entrar em um clube. */
    public static void solicitarEntrada(String clubeId) {
        enviar("POST", "/clubes/" + clubeId + "/membros", null);
    }

    /** O lider aprova (true) ou recusa (false) o pedido de um usuario. */
    public static void avaliarMembro(String clubeId, String usuarioId, boolean aprovado) {
        HashMap<String, Object> corpo = new HashMap<>();
        corpo.put("aprovado", aprovado);
        enviar("PUT", "/clubes/" + clubeId + "/membros/" + usuarioId + "/avaliacao", corpo);
    }

    // ----- Posts -----

    /** Lista os posts da timeline geral. */
    public static PostResumo[] timeline() {
        String resposta = enviar("GET", "/posts?pagina=0&tamanho=100", null);
        return (PostResumo[]) lerItens(resposta, PostResumo[].class);
    }

    /** Detalhe de um post, com a lista de comentarios. */
    public static PostDetalhe detalhePost(String postId) {
        String resposta = enviar("GET", "/posts/" + postId, null);
        return (PostDetalhe) lerObjeto(resposta, PostDetalhe.class);
    }

    /** Cria um post. clubeId pode ser null (post geral). */
    public static void criarPost(String conteudo, String clubeId, boolean anonimo) {
        HashMap<String, Object> corpo = new HashMap<>();
        corpo.put("conteudo", conteudo);
        corpo.put("clubeId", clubeId);
        corpo.put("anonimo", anonimo);
        enviar("POST", "/posts", corpo);
    }

    /** Da ou tira o "curtir" de um post. */
    public static void upvote(String postId) {
        enviar("PUT", "/posts/" + postId + "/upvote", null);
    }

    /** Adiciona um comentario a um post. */
    public static void comentar(String postId, String conteudo) {
        HashMap<String, Object> corpo = new HashMap<>();
        corpo.put("conteudo", conteudo);
        enviar("POST", "/posts/" + postId + "/comentarios", corpo);
    }

    // ----- Notificacoes e comunicados -----

    /** Lista as notificacoes recebidas pelo usuario. */
    public static NotificacaoResumo[] minhasNotificacoes() {
        String resposta = enviar("GET", "/comunicados/minhas?pagina=0&tamanho=100", null);
        return (NotificacaoResumo[]) lerItens(resposta, NotificacaoResumo[].class);
    }

    /** Marca uma notificacao como lida. */
    public static void marcarLida(String notificacaoId) {
        enviar("PATCH", "/comunicados/" + notificacaoId + "/lida", null);
    }

    /** Marca todas as notificacoes como lidas. */
    public static void marcarTodasLidas() {
        enviar("PATCH", "/comunicados/lidas", null);
    }

    /** Envia um comunicado. tipoAlvo e "GERAL" ou "CLUBE"; alvoId pode ser null. */
    public static void enviarComunicado(String titulo, String mensagem, String tipoAlvo, String alvoId) {
        HashMap<String, Object> corpo = new HashMap<>();
        corpo.put("titulo", titulo);
        corpo.put("mensagem", mensagem);
        corpo.put("tipoAlvo", tipoAlvo);
        corpo.put("alvoId", alvoId);
        enviar("POST", "/comunicados", corpo);
    }

    // ----- Parte interna -----

    /** Monta e envia um pedido HTTP; devolve o corpo da resposta como texto. */
    private static String enviar(String metodo, String caminho, Object corpo) {
        try {
            HttpRequest.Builder pedido = HttpRequest.newBuilder()
                    .uri(URI.create(BASE + caminho))
                    .header("Accept", "application/json");

            // Se ja existe alguem logado, manda o token junto no cabecalho.
            if (Sessao.token != null) {
                pedido.header("Authorization", "Bearer " + Sessao.token);
            }

            if (corpo != null) {
                String json = JSON.writeValueAsString(corpo);
                pedido.header("Content-Type", "application/json");
                pedido.method(metodo, HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8));
            } else {
                pedido.method(metodo, HttpRequest.BodyPublishers.noBody());
            }

            HttpResponse<String> resposta = HTTP.send(pedido.build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            int status = resposta.statusCode();
            if (status >= 200 && status < 300) {
                return resposta.body();
            }
            // joga uma mensagem para a tela mostrar.
            throw new RuntimeException(mensagemDeErro(resposta.body(), status));

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Nao foi possivel falar com o servidor. Veja se o backend esta ligado.");
        }
    }

    /** Converte o texto JSON em um objeto da classe pedida. */
    private static Object lerObjeto(String json, Class<?> classe) {
        try {
            return JSON.readValue(json, classe);
        } catch (Exception e) {
            throw new RuntimeException("Nao consegui entender a resposta do servidor.");
        }
    }

    /** Respostas paginadas vem dentro de um campo "itens"; pega so essa lista. */
    private static Object lerItens(String json, Class<?> classeDaLista) {
        try {
            JsonNode raiz = JSON.readTree(json);
            return JSON.treeToValue(raiz.get("itens"), classeDaLista);
        } catch (Exception e) {
            throw new RuntimeException("Nao consegui entender a resposta do servidor.");
        }
    }

    /** Tenta achar uma mensagem amigavel dentro do corpo de erro do servidor. */
    private static String mensagemDeErro(String corpo, int status) {
        try {
            JsonNode raiz = JSON.readTree(corpo);
            if (raiz.hasNonNull("erro")) {
                return raiz.get("erro").asText();
            }
            if (raiz.hasNonNull("message")) {
                return raiz.get("message").asText();
            }
        } catch (Exception ignorado) {
            // o corpo nao era JSON; usa a mensagem padrao abaixo
        }
        return "Algo deu errado (erro " + status + "). Tente novamente.";
    }
}
