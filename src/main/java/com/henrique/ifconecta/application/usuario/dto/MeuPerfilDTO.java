package com.henrique.ifconecta.application.usuario.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.henrique.ifconecta.domain.usuario.enums.RoleUsuario;
import com.henrique.ifconecta.domain.usuario.enums.StatusUsuario;

public record MeuPerfilDTO(
        UUID id,
        String nome,
        String emailAcad,
        RoleUsuario role,
        StatusUsuario status,
        UUID cursoId,
        LocalDateTime dataCriacao,
        String tipo,
        String prontuario,
        String siape,
        String setor,
        String cargo) {
}
