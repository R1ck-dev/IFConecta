package com.henrique.ifconecta.domain.academico.model;

import java.util.UUID;

public class Disciplina {
    private UUID id;
    private UUID cursoId;
    private String nome;
    private int cargaHoraria;

    public Disciplina(UUID cursoId, String nome, int cargaHoraria) {
        this.id = UUID.randomUUID();
        this.cursoId = cursoId;
        this.nome = nome;
        this.cargaHoraria = cargaHoraria;
    }

    public Disciplina(UUID id, UUID cursoId, String nome, int cargaHoraria) {
        this.id = id;
        this.cursoId = cursoId;
        this.nome = nome;
        this.cargaHoraria = cargaHoraria;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCursoId() {
        return cursoId;
    }

    public String getNome() {
        return nome;
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }

}
