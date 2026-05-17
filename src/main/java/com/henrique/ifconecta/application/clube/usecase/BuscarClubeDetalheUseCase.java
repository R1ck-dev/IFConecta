package com.henrique.ifconecta.application.clube.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.clube.dto.ClubeDetalheDTO;
import com.henrique.ifconecta.domain.clube.enums.PapelMembro;
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
public class BuscarClubeDetalheUseCase {

    private final ClubeRepository clubeRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public ClubeDetalheDTO execute(UUID clubeId, UUID usuarioCorrenteId) {
        Clube clube = clubeRepository.buscarPorId(clubeId)
                .orElseThrow(() -> new NegocioException("Clube não encontrado"));

        MembroClube lider = clube.getMembros().stream()
                .filter(m -> m.getPapel() == PapelMembro.LIDER && m.getStatus() == StatusMembro.APROVADO)
                .findFirst()
                .orElseThrow(() -> new NegocioException("Clube sem líder ativo"));

        Usuario liderUsuario = usuarioRepository.buscarPorId(lider.getUsuarioId())
                .orElseThrow(() -> new NegocioException("Líder do clube não encontrado"));

        StatusMembro minhaSituacao = clube.getMembros().stream()
                .filter(m -> m.getUsuarioId().equals(usuarioCorrenteId))
                .map(MembroClube::getStatus)
                .findFirst()
                .orElse(null);

        return new ClubeDetalheDTO(
                clube.getId(),
                clube.getNome(),
                clube.getDescricao(),
                clube.getTipoAcesso(),
                clube.getMembrosAprovados().size(),
                liderUsuario.getId(),
                liderUsuario.getNome(),
                clube.isMembroAprovado(usuarioCorrenteId),
                clube.isLider(usuarioCorrenteId),
                minhaSituacao
        );
    }
}
