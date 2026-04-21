package com.henrique.ifconecta.domain.usuario.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.henrique.ifconecta.domain.usuario.enums.RoleUsuario;
import com.henrique.ifconecta.domain.usuario.enums.StatusUsuario;

public class Professor extends Usuario{
    private String siape;

    // Construtor de Criação
    public Professor(UUID id, String nome, String emailAcad, String senhaHash, RoleUsuario role, String siape) {
        super(id, nome, emailAcad, senhaHash, RoleUsuario.USER);
        this.siape = siape;
    }

    // Construtor de Reconstrução
    public Professor(UUID id, String nome, String emailAcad, String senhaHash, StatusUsuario status, RoleUsuario role, LocalDateTime dataCriacao, String siape) {
        super(id, nome, emailAcad, senhaHash, status, role, dataCriacao);
        this.siape = siape;
    }

    public String getSiape() {
        return siape;
    }
    
}
