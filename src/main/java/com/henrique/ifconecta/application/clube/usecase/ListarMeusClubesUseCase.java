package com.henrique.ifconecta.application.clube.usecase;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.clube.dto.ClubeResumoDTO;
import com.henrique.ifconecta.domain.clube.port.ClubeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListarMeusClubesUseCase {

    private final ClubeRepository clubeRepository;

    @Transactional
    public List<ClubeResumoDTO> execute(UUID usuarioId) {
        return clubeRepository.listarClubesPorMembroAprovado(usuarioId).stream()
                .map(clube -> new ClubeResumoDTO(
                        clube.getId(),
                        clube.getNome(),
                        clube.getDescricao(),
                        clube.getTipoAcesso(),
                        clube.getMembrosAprovados().size()))
                .collect(Collectors.toList());
    }
}
