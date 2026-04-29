package com.henrique.ifconecta.domain.usuario.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.henrique.ifconecta.domain.usuario.enums.RoleUsuario;
import com.henrique.ifconecta.domain.usuario.enums.StatusUsuario;

public class Aluno extends Usuario{
    private String prontuario;

    // Construtor de Criação
    public Aluno(UUID id, UUID cursoId, String nome, String emailAcad, String senhaHash, String prontuario) {
        super(id, cursoId, nome, emailAcad, senhaHash, RoleUsuario.USER);
        this.prontuario = prontuario;
    }

    // Construtor de Reconstituição
    public Aluno(UUID id, UUID cursoId, String nome, String emailAcad, String senhaHash, StatusUsuario status, RoleUsuario role, LocalDateTime dataCriacao, String prontuario) {
        super(id, cursoId, nome, emailAcad, senhaHash, status, role, dataCriacao);
        this.prontuario = prontuario;
    }

    public String getProntuario() {
        return prontuario;
    }

}
