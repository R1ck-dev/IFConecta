package com.henrique.ifconecta.application.notificacao.usecase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.notificacao.dto.EnviarComunicadoInput;
import com.henrique.ifconecta.domain.academico.model.Turma;
import com.henrique.ifconecta.domain.academico.port.TurmaRepository;
import com.henrique.ifconecta.domain.clube.enums.PapelMembro;
import com.henrique.ifconecta.domain.clube.model.Clube;
import com.henrique.ifconecta.domain.clube.port.ClubeRepository;
import com.henrique.ifconecta.domain.notificacao.model.Notificacao;
import com.henrique.ifconecta.domain.notificacao.port.NotificacaoRepository;
import com.henrique.ifconecta.domain.usuario.enums.RoleUsuario;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;
import com.henrique.ifconecta.domain.usuario.model.Institucional;
import com.henrique.ifconecta.domain.usuario.model.Usuario;
import com.henrique.ifconecta.domain.usuario.port.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnviarComunicadoUseCase {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TurmaRepository turmaRepository;
    private final ClubeRepository clubeRepository;

    @Transactional
    public void execute(EnviarComunicadoInput input) {
        Usuario remetente = usuarioRepository.buscarPorId(input.remetenteId())
                .orElseThrow(() -> new NegocioException("Remetente não encontrado."));

        List<UUID> destinatariosIds = new ArrayList<>();

        // 1. Validação de Segurança e Resgate de Audiência (Fan-Out)
        switch (input.tipoAlvo()) {
            case GERAL -> {
                validarPermissaoGlobal(remetente);
                destinatariosIds = usuarioRepository.buscarTodosIdsAtivos();
            }
            case CURSO -> {
                validarPermissaoGlobal(remetente);
                if (input.alvoId() == null)
                    throw new NegocioException("ID do Curso é obrigatório.");
                destinatariosIds = usuarioRepository.buscarIdsPorCurso(input.alvoId());
            }
            case TURMA -> {
                if (input.alvoId() == null)
                    throw new NegocioException("ID da Turma é obrigatório.");
                Turma turma = turmaRepository.buscarPorId(input.alvoId())
                        .orElseThrow(() -> new NegocioException("Turma não encontrada."));

                if (!turma.getProfessorId().equals(remetente.getId())) {
                    throw new NegocioException(
                            "Apenas o professor responsável pode enviar comunicados para esta turma.");
                }
                destinatariosIds = new ArrayList<>(turma.getAlunosMatriculados());
            }
            case CLUBE -> {
                if (input.alvoId() == null)
                    throw new NegocioException("ID do Clube é obrigatório.");
                Clube clube = clubeRepository.buscarPorId(input.alvoId())
                        .orElseThrow(() -> new NegocioException("Clube não encontrado."));

                boolean isLider = clube.getMembros().stream()
                        .anyMatch(m -> m.getUsuarioId().equals(remetente.getId()) && m.getPapel() == PapelMembro.LIDER);

                if (!isLider) {
                    throw new NegocioException("Apenas o líder pode enviar comunicados para todo o clube.");
                }

                // Pega todos os membros aprovados
                destinatariosIds = clube.getMembros().stream()
                        .map(m -> m.getUsuarioId())
                        .collect(Collectors.toList());
            }
        }

        // 2. Geração em Massa
        if (destinatariosIds.isEmpty()) {
            throw new NegocioException("Nenhum destinatário encontrado para este alvo.");
        }

        List<Notificacao> notificacoesGeradas = destinatariosIds.stream()
                // Evita que o remetente notifique a si mesmo se ele fizer parte do grupo
                .filter(id -> !id.equals(remetente.getId()))
                .map(id -> new Notificacao(
                        id,
                        remetente.getId(),
                        input.titulo(),
                        input.mensagem(),
                        input.tipoAlvo(),
                        input.alvoId()))
                .collect(Collectors.toList());

        // 3. Persistência
        notificacaoRepository.salvarEmLote(notificacoesGeradas);
    }

    private void validarPermissaoGlobal(Usuario remetente) {
        if (remetente.getRole() != RoleUsuario.ADMIN && !(remetente instanceof Institucional)) {
            throw new NegocioException(
                    "Apenas membros institucionais ou administradores podem enviar comunicados gerais ou por curso.");
        }
    }
}
