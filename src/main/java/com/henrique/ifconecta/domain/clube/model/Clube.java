package com.henrique.ifconecta.domain.clube.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.henrique.ifconecta.domain.clube.enums.PapelMembro;
import com.henrique.ifconecta.domain.clube.enums.StatusClube;

public class Clube {
    private UUID id;
    private String nome;
    private String descricao;
    private StatusClube status;
    private LocalDateTime dataCriacao;
    private List<MembroClube> membros;

    public Clube(String nome, String descricao, UUID criadorId) {
        this.id = UUID.randomUUID();
        this.nome = nome;
        this.descricao = descricao;
        this.status = StatusClube.ATIVO;
        this.dataCriacao = LocalDateTime.now();
        this.membros = new ArrayList<>();
        this.membros.add(new MembroClube(criadorId, PapelMembro.LIDER));
    }

    public Clube(UUID id, String nome, String descricao, StatusClube status,
            LocalDateTime dataCriacao, List<MembroClube> membros) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.membros = (membros != null) ? membros : new ArrayList<>();
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public StatusClube getStatus() { return status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public List<MembroClube> getMembros() { return membros; }
}
