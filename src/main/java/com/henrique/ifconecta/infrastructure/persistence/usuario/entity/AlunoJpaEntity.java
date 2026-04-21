package com.henrique.ifconecta.infrastructure.persistence.usuario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "alunos")
@PrimaryKeyJoinColumn(name = "usuario_id")
/**
 * Em tabelas normais, usariamos o @Id e @GeneratedValue para o banco criar o ID. Mas na estratégia JOINED, o Aluno não gera um ID novo. o ID do aluno tem que ser exatamente o mesmo ID da tabela Usuario. Essa anotação serve justamente para isso.
 */
@Getter
@Setter
public class AlunoJpaEntity extends UsuarioJpaEntity{
    @Column(nullable = false, unique = true)
    private String prontuario;
}
