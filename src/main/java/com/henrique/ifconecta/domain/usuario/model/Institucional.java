package com.henrique.ifconecta.domain.usuario.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.henrique.ifconecta.domain.usuario.enums.RoleUsuario;
import com.henrique.ifconecta.domain.usuario.enums.StatusUsuario;

public class Institucional extends Usuario{
    private String setor;
    private String cargo;

    // Construtor de Criação
    public Institucional(UUID id, String nome, String emailAcad, String senhaHash, String setor, String cargo) {
        super(id, nome, emailAcad, senhaHash, RoleUsuario.USER);
        this.setor = setor;
        this.cargo = cargo;
    }

    // Construtor de Reconstituição
    public Institucional(UUID id, String nome, String emailAcad, String senhaHash, StatusUsuario status, RoleUsuario role, LocalDateTime dataCriacao, String setor, String cargo) {
        super(id, nome, emailAcad, senhaHash, status, role, dataCriacao);
        this.setor = setor;
        this.cargo = cargo;
    }

    public String getSetor() {
        return setor;
    }

    public String getCargo() {
        return cargo;
    }

}
