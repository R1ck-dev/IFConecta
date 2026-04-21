package com.henrique.ifconecta.infrastructure.persistence.usuario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "professores")
@PrimaryKeyJoinColumn(name = "usuario_id")
@Getter
@Setter
public class ProfessorJpaEntity extends UsuarioJpaEntity{
    @Column(nullable = false, unique = true)
    private String siape;
}
