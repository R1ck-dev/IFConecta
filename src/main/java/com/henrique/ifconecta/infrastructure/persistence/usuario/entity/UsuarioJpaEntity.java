package com.henrique.ifconecta.infrastructure.persistence.usuario.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.henrique.ifconecta.domain.usuario.enums.RoleUsuario;
import com.henrique.ifconecta.domain.usuario.enums.StatusUsuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
/**
 * O banco relacional não entende herança orientada a objetos, utilizamos essa anotação para ensinar ao hibernate a quebrar a herança em diferentes tabelas. Fazendo com que o hibernate crie uma tabela pai "usuarios" com as colunas comuns e tabelas filhas "alunos", "professores", apenas com as colunas especificas
 */
@Getter
@Setter
public class UsuarioJpaEntity {
    @Id
    private UUID id;

    @Column(name = "curso_id")
    private UUID cursoId;
    
    @Column(nullable = false)
    private String nome;

    @Column(name = "email_acad", nullable = false, unique = true)
    private String emailAcad;
    
    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusUsuario status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleUsuario role;
    
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
}
