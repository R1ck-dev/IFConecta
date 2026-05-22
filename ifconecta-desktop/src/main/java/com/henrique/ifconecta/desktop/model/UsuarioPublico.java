package com.henrique.ifconecta.desktop.model;

/** Espelha UsuarioPublicoDTO (GET /api/usuarios/professores e /{id}). */
public record UsuarioPublico(
        String id,
        String nome,
        String role,
        String tipo,
        String cursoId) {
}
