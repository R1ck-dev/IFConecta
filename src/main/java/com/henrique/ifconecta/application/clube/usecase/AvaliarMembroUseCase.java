package com.henrique.ifconecta.application.clube.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.domain.clube.model.Clube;
import com.henrique.ifconecta.domain.clube.port.ClubeRepository;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvaliarMembroUseCase {
    
    private final ClubeRepository clubeRepository;

    @Transactional
    public void execute(UUID clubeId, UUID liderId, UUID usuarioAlvoId, boolean aprovado) {
        Clube clube = clubeRepository.buscarPorId(clubeId)
                .orElseThrow(() -> new NegocioException("Clube não encontrado"));

        clube.avaliarSolicitacao(liderId, usuarioAlvoId, aprovado);

        clubeRepository.salvar(clube);
    }
}
