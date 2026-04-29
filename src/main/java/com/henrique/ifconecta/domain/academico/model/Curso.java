package com.henrique.ifconecta.domain.academico.model;

import java.util.UUID;

import com.henrique.ifconecta.domain.academico.enums.ModalidadeCurso;

public class Curso {
    private UUID id;
    private String nome;
    private String sigla;
    private ModalidadeCurso modalidade;

    public Curso(String nome, String sigla, ModalidadeCurso modalidade) {
        this.id = UUID.randomUUID();
        this.nome = nome;
        this.sigla = sigla;
        this.modalidade = modalidade;
    }

    public Curso(UUID id, String nome, String sigla, ModalidadeCurso modalidade) {
        this.id = id;
        this.nome = nome;
        this.sigla = sigla;
        this.modalidade = modalidade;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getSigla() {
        return sigla;
    }

    public ModalidadeCurso getModalidade() {
        return modalidade;
    }

}
