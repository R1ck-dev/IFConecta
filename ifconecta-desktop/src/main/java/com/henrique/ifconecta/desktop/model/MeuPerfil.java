package com.henrique.ifconecta.desktop.model;

/**
 * Espelha MeuPerfilDTO (GET /api/usuarios/me).
 * UUIDs e datas ficam como String — basta para exibição no cliente.
 */
public record MeuPerfil(
        String id,
        String nome,
        String emailAcad,
        String role,
        String status,
        String dataCriacao,
        String tipo,
        String prontuario,
        String siape,
        String setor,
        String cargo) {

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    /** Equivale a podeComunicar() do front web (professores e institucionais). */
    public boolean podeComunicar() {
        return "PROFESSOR".equalsIgnoreCase(tipo) || "INSTITUCIONAL".equalsIgnoreCase(tipo);
    }

    /** Equivale a tipoLabel() do front web. */
    public String tipoLabel() {
        if ("PROFESSOR".equalsIgnoreCase(tipo)) return "Professor(a)";
        if ("INSTITUCIONAL".equalsIgnoreCase(tipo)) return "Servidor(a)";
        return "Aluno(a)";
    }

    public String primeiroNome() {
        if (nome == null || nome.isBlank()) return "colega";
        return nome.trim().split("\\s+")[0];
    }
}
