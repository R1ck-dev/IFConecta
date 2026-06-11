package com.henrique.ifconecta.desktop.model;

import java.time.LocalDateTime;

public record MembroClube(
        String usuarioId,
        String nome,
        String tipo,
        String papel,
        LocalDateTime dataIngresso) {

    public boolean isLider() {
        return "LIDER".equalsIgnoreCase(papel);
    }
}
