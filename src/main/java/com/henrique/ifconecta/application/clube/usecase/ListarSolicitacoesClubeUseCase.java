package com.henrique.ifconecta.application.clube.usecase;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.clube.dto.SolicitacaoMembroDTO;
import com.henrique.ifconecta.domain.clube.enums.StatusMembro;
import com.henrique.ifconecta.domain.clube.model.Clube;
import com.henrique.ifconecta.domain.clube.model.MembroClube;
import com.henrique.ifconecta.domain.clube.port.ClubeRepository;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;
import com.henrique.ifconecta.domain.usuario.model.Usuario;
import com.henrique.ifconecta.domain.usuario.port.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListarSolicitacoesClubeUseCase {

    private final ClubeRepository clubeRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public List<SolicitacaoMembroDTO> execute(UUID clubeId, UUID liderId) {
        Clube clube = clubeRepository.buscarPorId(clubeId)
                .orElseThrow(() -> new NegocioException("Clube não encontrado"));

        if (!clube.isLider(liderId)) {
            throw new NegocioException("Apenas o líder do clube pode consultar as solicitações pendentes.");
        }

        List<MembroClube> pendentes = clube.getMembros().stream()
                .filter(m -> m.getStatus() == StatusMembro.PENDENTE)
                .collect(Collectors.toList());

        if (pendentes.isEmpty()) {
            return List.of();
        }

        List<UUID> ids = pendentes.stream().map(MembroClube::getUsuarioId).collect(Collectors.toList());
        Map<UUID, Usuario> usuariosPorId = usuarioRepository.buscarPorIds(ids).stream()
                .collect(Collectors.toMap(Usuario::getId, Function.identity()));

        return pendentes.stream()
                .map(m -> {
                    Usuario u = usuariosPorId.get(m.getUsuarioId());
                    return new SolicitacaoMembroDTO(
                            m.getUsuarioId(),
                            u != null ? u.getNome() : "Usuário desconhecido",
                            m.getDataIngresso());
                })
                .collect(Collectors.toList());
    }
}
