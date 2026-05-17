package com.henrique.ifconecta.application.notificacao.usecase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.notificacao.dto.EnviarComunicadoInput;
import com.henrique.ifconecta.domain.academico.model.Turma;
import com.henrique.ifconecta.domain.academico.port.TurmaRepository;
import com.henrique.ifconecta.domain.clube.model.Clube;
import com.henrique.ifconecta.domain.clube.port.ClubeRepository;
import com.henrique.ifconecta.domain.notificacao.model.Notificacao;
import com.henrique.ifconecta.domain.notificacao.port.NotificacaoRepository;
import com.henrique.ifconecta.domain.usuario.enums.RoleUsuario;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;
import com.henrique.ifconecta.domain.usuario.model.Institucional;
import com.henrique.ifconecta.domain.usuario.model.Professor;
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

                boolean isAdminOuInstitucional = temPapelGlobal(remetente);
                boolean isProfessorResponsavel = turma.getProfessorId().equals(remetente.getId());
                if (!isAdminOuInstitucional && !isProfessorResponsavel) {
                    throw new NegocioException(
                            "Apenas admin, institucional ou o professor responsável pode enviar comunicados para esta turma.");
                }
                destinatariosIds = new ArrayList<>(turma.getAlunosMatriculados());
            }
            case CLUBE -> {
                if (input.alvoId() == null)
                    throw new NegocioException("ID do Clube é obrigatório.");
                Clube clube = clubeRepository.buscarPorId(input.alvoId())
                        .orElseThrow(() -> new NegocioException("Clube não encontrado."));

                boolean isAdminOuInstitucional = temPapelGlobal(remetente);
                boolean isLider = clube.isLider(remetente.getId());
                boolean isProfessorMembro = remetente instanceof Professor
                        && clube.isMembroAprovado(remetente.getId());
                if (!isAdminOuInstitucional && !isLider && !isProfessorMembro) {
                    throw new NegocioException(
                            "Apenas admin, institucional, líder ou professor membro pode enviar comunicados para este clube.");
                }

                destinatariosIds = clube.obterIdsMembrosAprovados();
            }
        }

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

        notificacaoRepository.salvarEmLote(notificacoesGeradas);
    }

    private void validarPermissaoGlobal(Usuario remetente) {
        if (!temPapelGlobal(remetente)) {
            throw new NegocioException(
                    "Apenas membros institucionais ou administradores podem enviar comunicados gerais ou por curso.");
        }
    }

    private boolean temPapelGlobal(Usuario remetente) {
        return remetente.getRole() == RoleUsuario.ADMIN || remetente instanceof Institucional;
    }
}
