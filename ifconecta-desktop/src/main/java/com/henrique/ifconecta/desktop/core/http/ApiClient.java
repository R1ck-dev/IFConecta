package com.henrique.ifconecta.desktop.core.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.henrique.ifconecta.desktop.core.Session;

import javafx.application.Platform;

/**
 * Cliente HTTP REST — equivale ao módulo services/api.js (axios) do front web.
 *
 * <p>Resolve a URL base, injeta o header {@code Authorization: Bearer},
 * (de)serializa JSON com Jackson e centraliza o tratamento de erros.
 * Em 401 dispara o handler de não-autorizado (volta ao login).</p>
 *
 * <p>Os métodos são bloqueantes — devem ser chamados de uma thread de
 * trabalho (ver {@code AsyncRunner}), nunca da FX Application Thread.</p>
 */
public final class ApiClient {

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final String BASE_URL = resolveBaseUrl();

    /** Acionado quando o backend responde 401 — configurado por IFConectaApp. */
    private static volatile Runnable unauthorizedHandler = () -> { };

    private ApiClient() {
    }

    public static ObjectMapper mapper() {
        return MAPPER;
    }

    public static String baseUrl() {
        return BASE_URL;
    }

    public static void setUnauthorizedHandler(Runnable handler) {
        unauthorizedHandler = handler;
    }

    private static String resolveBaseUrl() {
        String env = System.getenv("IFCONECTA_API_URL");
        String base = (env == null || env.isBlank()) ? "http://localhost:8080" : env.trim();
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "/api";
    }

    // ───────── API pública ─────────

    public static <T> T get(String path, Class<T> type) {
        return read(exchange("GET", path, null), MAPPER.getTypeFactory().constructType(type));
    }

    public static <T> T get(String path, TypeReference<T> type) {
        return read(exchange("GET", path, null), MAPPER.getTypeFactory().constructType(type));
    }

    public static <T> T post(String path, Object body, Class<T> type) {
        return read(exchange("POST", path, body), MAPPER.getTypeFactory().constructType(type));
    }

    public static void post(String path, Object body) {
        exchange("POST", path, body);
    }

    public static void put(String path) {
        exchange("PUT", path, null);
    }

    public static void patch(String path, Object body) {
        exchange("PATCH", path, body);
    }

    public static void delete(String path) {
        exchange("DELETE", path, null);
    }

    // ───────── Núcleo ─────────

    private static <T> T read(String body, JavaType type) {
        if (body == null || body.isBlank()) {
            return null;
        }
        try {
            return MAPPER.readValue(body, type);
        } catch (Exception e) {
            throw new ApiException(0, "Resposta inválida do servidor.");
        }
    }

    private static String exchange(String method, String path, Object body) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .timeout(Duration.ofSeconds(30))
                    .header("Accept", "application/json");

            String token = Session.get().token();
            if (token != null) {
                builder.header("Authorization", "Bearer " + token);
            }

            if (body != null) {
                String json = MAPPER.writeValueAsString(body);
                builder.header("Content-Type", "application/json");
                builder.method(method, HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8));
            } else {
                builder.method(method, HttpRequest.BodyPublishers.noBody());
            }

            HttpResponse<String> response = HTTP.send(builder.build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            int status = response.statusCode();

            if (status >= 200 && status < 300) {
                return response.body();
            }
            if (status == 401) {
                // Só aciona o redirecionamento se havia uma sessão ativa —
                // assim um 401 de credenciais inválidas no login não recarrega a tela.
                if (Session.get().authenticated()) {
                    Platform.runLater(unauthorizedHandler);
                }
                throw new ApiException(401, "Sua sessão expirou. Entre novamente.");
            }
            throw new ApiException(status, extractError(response.body(), status));

        } catch (ApiException e) {
            throw e;
        } catch (IOException e) {
            throw new ApiException(0, "Não foi possível conectar ao servidor. Verifique se o backend está no ar.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(0, "Requisição interrompida.");
        } catch (Exception e) {
            throw new ApiException(0, "Erro inesperado: " + e.getMessage());
        }
    }

    /** Extrai a mensagem do corpo de erro — equivale a extractErrorMessage() do front web. */
    private static String extractError(String body, int status) {
        if (body != null && !body.isBlank()) {
            try {
                JsonNode node = MAPPER.readTree(body);
                if (node.hasNonNull("erro")) {
                    return node.get("erro").asText();
                }
                if (node.hasNonNull("message")) {
                    return node.get("message").asText();
                }
                if (node.isObject()) {
                    var fields = node.fields();
                    if (fields.hasNext()) {
                        JsonNode first = fields.next().getValue();
                        if (first.isTextual()) {
                            return first.asText();
                        }
                    }
                }
                if (node.isTextual()) {
                    return node.asText();
                }
            } catch (Exception ignored) {
                // corpo não-JSON — usa mensagem genérica
            }
        }
        return "Algo deu errado (HTTP " + status + "). Tente novamente.";
    }
}
