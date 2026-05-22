package com.henrique.ifconecta.desktop.service;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.henrique.ifconecta.desktop.core.http.ApiClient;
import com.henrique.ifconecta.desktop.model.ClubeDetalhe;
import com.henrique.ifconecta.desktop.model.ClubeResumo;
import com.henrique.ifconecta.desktop.model.MembroClube;
import com.henrique.ifconecta.desktop.model.Pagina;
import com.henrique.ifconecta.desktop.model.PostResumo;
import com.henrique.ifconecta.desktop.model.SolicitacaoMembro;

/** Endpoints de clubes — espelha services/clubes.js do front web. */
public final class ClubeService {

    private ClubeService() {
    }

    /** GET /api/clubes?pagina=&tamanho= */
    public static Pagina<ClubeResumo> listar(int pagina, int tamanho) {
        return ApiClient.get("/clubes?pagina=" + pagina + "&tamanho=" + tamanho,
                new TypeReference<Pagina<ClubeResumo>>() {
                });
    }

    /** GET /api/clubes/meus */
    public static List<ClubeResumo> meusClubes() {
        return ApiClient.get("/clubes/meus", new TypeReference<List<ClubeResumo>>() {
        });
    }

    /** GET /api/clubes/{id} */
    public static ClubeDetalhe detalhe(String clubeId) {
        return ApiClient.get("/clubes/" + clubeId, ClubeDetalhe.class);
    }

    /** GET /api/clubes/{id}/membros */
    public static List<MembroClube> membros(String clubeId) {
        return ApiClient.get("/clubes/" + clubeId + "/membros", new TypeReference<List<MembroClube>>() {
        });
    }

    /** GET /api/clubes/{id}/solicitacoes — apenas para o líder. */
    public static List<SolicitacaoMembro> solicitacoes(String clubeId) {
        return ApiClient.get("/clubes/" + clubeId + "/solicitacoes", new TypeReference<List<SolicitacaoMembro>>() {
        });
    }

    /** GET /api/clubes/{id}/posts?pagina=&tamanho= */
    public static Pagina<PostResumo> posts(String clubeId, int pagina, int tamanho) {
        return ApiClient.get("/clubes/" + clubeId + "/posts?pagina=" + pagina + "&tamanho=" + tamanho,
                new TypeReference<Pagina<PostResumo>>() {
                });
    }

    /** POST /api/clubes */
    public static void criar(String nome, String descricao, String tipoAcesso) {
        ApiClient.post("/clubes", Map.of("nome", nome, "descricao", descricao, "tipoAcesso", tipoAcesso));
    }

    /** POST /api/clubes/{id}/membros — solicita entrada. */
    public static void solicitarEntrada(String clubeId) {
        ApiClient.post("/clubes/" + clubeId + "/membros", null);
    }

    /** PUT /api/clubes/{id}/membros/{usuarioId}/avaliacao */
    public static void avaliarMembro(String clubeId, String usuarioId, boolean aprovado) {
        ApiClient.put("/clubes/" + clubeId + "/membros/" + usuarioId + "/avaliacao",
                Map.of("aprovado", aprovado));
    }
}
