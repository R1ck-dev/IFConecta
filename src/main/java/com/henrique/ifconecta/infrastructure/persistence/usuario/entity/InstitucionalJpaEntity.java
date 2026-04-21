package com.henrique.ifconecta.infrastructure.persistence.usuario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "institucionais")
@PrimaryKeyJoinColumn(name = "usuario_id")
@Getter
@Setter
public class InstitucionalJpaEntity extends UsuarioJpaEntity{
    @Column(nullable = false)
    private String setor;

    @Column(nullable = false)
    private String cargo;
}
