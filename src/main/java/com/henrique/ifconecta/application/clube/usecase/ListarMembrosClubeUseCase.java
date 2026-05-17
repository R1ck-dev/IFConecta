package com.henrique.ifconecta.application.clube.usecase;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.clube.dto.MembroClubeDTO;
import com.henrique.ifconecta.domain.clube.model.Clube;
import com.henrique.ifconecta.domain.clube.model.MembroClube;
import com.henrique.ifconecta.domain.clube.port.ClubeRepository;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;
import com.henrique.ifconecta.domain.usuario.model.Aluno;
import com.henrique.ifconecta.domain.usuario.model.Institucional;
import com.henrique.ifconecta.domain.usuario.model.Professor;
import com.henrique.ifconecta.domain.usuario.model.Usuario;
import com.henrique.ifconecta.domain.usuario.port.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListarMembrosClubeUseCase {

    private final ClubeRepository clubeRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public List<MembroClubeDTO> execute(UUID clubeId, UUID usuarioId) {
        Clube clube = clubeRepository.buscarPorId(clubeId)
                .orElseThrow(() -> new NegocioException("Clube não encontrado."));

        if (!clube.isMembroAprovado(usuarioId)) {
            throw new NegocioException("Apenas membros aprovados podem ver os membros deste clube.");
        }

        List<MembroClube> aprovados = clube.getMembrosAprovados();
        if (aprovados.isEmpty()) {
            return List.of();
        }

        List<UUID> ids = aprovados.stream().map(MembroClube::getUsuarioId).collect(Collectors.toList());
        Map<UUID, Usuario> usuariosPorId = usuarioRepository.buscarPorIds(ids).stream()
                .collect(Collectors.toMap(Usuario::getId, Function.identity()));

        return aprovados.stream()
                .map(m -> {
                    Usuario u = usuariosPorId.get(m.getUsuarioId());
                    return new MembroClubeDTO(
                            m.getUsuarioId(),
                            u != null ? u.getNome() : "Usuário desconhecido",
                            tipoDe(u),
                            m.getPapel(),
                            m.getDataIngresso());
                })
                .collect(Collectors.toList());
    }

    private String tipoDe(Usuario u) {
        if (u instanceof Aluno) return "ALUNO";
        if (u instanceof Professor) return "PROFESSOR";
        if (u instanceof Institucional) return "INSTITUCIONAL";
        return null;
    }
}
