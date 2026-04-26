package com.henrique.ifconecta.domain.clube.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.henrique.ifconecta.domain.clube.enums.PapelMembro;
import com.henrique.ifconecta.domain.clube.enums.StatusClube;
import com.henrique.ifconecta.domain.clube.enums.StatusMembro;
import com.henrique.ifconecta.domain.clube.enums.TipoAcesso;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;

public class Clube {
    private UUID id;
    private String nome;
    private String descricao;
    private StatusClube status;
    private TipoAcesso tipoAcesso;
    private LocalDateTime dataCriacao;
    private List<MembroClube> membros;

    // Construtor de Criação (um clube nunca nasce vazio, quem cria vira o líder)
    public Clube(String nome, String descricao, TipoAcesso tipoAcesso, UUID criadorId) {
        this.id = UUID.randomUUID();
        this.nome = nome;
        this.descricao = descricao;
        this.status = StatusClube.ATIVO;
        this.tipoAcesso = tipoAcesso;
        this.dataCriacao = LocalDateTime.now();

        this.membros = new ArrayList<>();
        // O criador é líder e aprovado automaticamente
        this.membros.add(new MembroClube(criadorId, PapelMembro.LIDER, StatusMembro.APROVADO));
    }

    // Construtor de Reconstituição
    public Clube(UUID id, String nome, String descricao, StatusClube status, TipoAcesso tipoAcesso,
            LocalDateTime dataCriacao, List<MembroClube> membros) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.status = status;
        this.tipoAcesso = tipoAcesso;
        this.dataCriacao = dataCriacao;
        this.membros = (membros != null) ? membros : new ArrayList<>();
    }

    public void solicitarEntrada(UUID usuarioId) {
        boolean jaEhMembro = membros.stream().anyMatch(userId -> userId.getUsuarioId().equals(usuarioId));

        if (jaEhMembro) {
            throw new NegocioException("O usuário já faz parte deste clube ou tem um convite pendente");
        }

        StatusMembro statusInicial = (this.tipoAcesso == TipoAcesso.PUBLICO) ? StatusMembro.APROVADO
                : StatusMembro.PENDENTE;

        MembroClube novoMembro = new MembroClube(usuarioId, PapelMembro.MEMBRO, statusInicial);
        this.membros.add(novoMembro);
    }

    public void avaliarSolicitacao(UUID liderId, UUID usuarioAlvoId, boolean aprovado) {
        boolean isLider = this.membros.stream()
                .anyMatch(member -> member.getUsuarioId().equals(liderId) && member.getPapel() == PapelMembro.LIDER);

        if (!isLider) {
            throw new NegocioException("Apenas o líder do clube pode avaliar solicitações de membros.");
        }

        MembroClube membroAlvo = this.membros.stream()
                .filter(member -> member.getUsuarioId().equals(usuarioAlvoId))
                .findFirst()
                .orElseThrow(() -> new NegocioException("Usuário não encontrado na lista de membros deste clube"));

        if (membroAlvo.getStatus() != StatusMembro.PENDENTE) {
            throw new NegocioException("Apenas membros com status PENDENTE podem ser avaliados.");
        }

        if (aprovado) {
            membroAlvo.aprovar();
        } else {
            membroAlvo.rejeitar();
        }
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public StatusClube getStatus() {
        return status;
    }

    public TipoAcesso getTipoAcesso() {
        return tipoAcesso;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public List<MembroClube> getMembros() {
        return membros;
    }

}
