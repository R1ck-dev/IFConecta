package com.henrique.ifconecta.desktop.service;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.henrique.ifconecta.desktop.core.http.ApiClient;
import com.henrique.ifconecta.desktop.model.NotificacaoResumo;
import com.henrique.ifconecta.desktop.model.Pagina;

/** Endpoints de comunicados/notificações — espelha services/notificacoes.js do front web. */
public final class NotificacaoService {

    private NotificacaoService() {
    }

    /** GET /api/comunicados/minhas?pagina=&tamanho= */
    public static Pagina<NotificacaoResumo> listar(int pagina, int tamanho) {
        return ApiClient.get("/comunicados/minhas?pagina=" + pagina + "&tamanho=" + tamanho,
                new TypeReference<Pagina<NotificacaoResumo>>() {
                });
    }

    /** GET /api/comunicados/minhas/nao-lidas/contagem */
    public static long contarNaoLidas() {
        Map<String, Long> resposta = ApiClient.get("/comunicados/minhas/nao-lidas/contagem",
                new TypeReference<Map<String, Long>>() {
                });
        return resposta == null ? 0 : resposta.getOrDefault("total", 0L);
    }

    /** PATCH /api/comunicados/{id}/lida */
    public static void marcarComoLida(String notificacaoId) {
        ApiClient.patch("/comunicados/" + notificacaoId + "/lida", null);
    }

    /** PATCH /api/comunicados/lidas — marca todas como lidas. */
    public static void marcarTodasComoLidas() {
        ApiClient.patch("/comunicados/lidas", null);
    }

    /** POST /api/comunicados — envia um comunicado. alvoId pode ser nulo (alvo GERAL). */
    public static void enviarComunicado(String titulo, String mensagem, String tipoAlvo, String alvoId) {
        Map<String, Object> body = new HashMap<>();
        body.put("titulo", titulo);
        body.put("mensagem", mensagem);
        body.put("tipoAlvo", tipoAlvo);
        body.put("alvoId", alvoId);
        ApiClient.post("/comunicados", body);
    }
}
