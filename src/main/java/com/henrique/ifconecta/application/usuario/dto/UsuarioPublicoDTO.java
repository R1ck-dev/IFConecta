package com.henrique.ifconecta.application.usuario.dto;

import java.util.UUID;

import com.henrique.ifconecta.domain.usuario.enums.RoleUsuario;

public record UsuarioPublicoDTO(
        UUID id,
        String nome,
        RoleUsuario role,
        String tipo,
        UUID cursoId) {
}
