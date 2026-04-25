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
public class SolicitarEntradaClubeUseCase {
    

    private final ClubeRepository clubeRepository;

    @Transactional
    public void execute(UUID clubeId, UUID usuarioId) {
        Clube clube = clubeRepository.buscarPorId(clubeId)
                .orElseThrow(() -> new NegocioException("Clube não encontrado."));

        clube.solicitarEntrada(usuarioId);

        clubeRepository.salvar(clube);
    }
}
