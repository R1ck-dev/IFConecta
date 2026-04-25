package com.henrique.ifconecta.application.clube.usecase;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.clube.dto.CriarClubeInput;
import com.henrique.ifconecta.domain.clube.model.Clube;
import com.henrique.ifconecta.domain.clube.port.ClubeRepository;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CriarClubeUseCase {
    
    private final ClubeRepository clubeRepository;

    @Transactional
    public void execute(CriarClubeInput input) {
        if (clubeRepository.existePorNome(input.nome())) {
            throw new NegocioException("Já existe um clube registrado com este nome.");
        }

        Clube novoClube = new Clube(
            input.nome(),
            input.descricao(),
            input.tipoAcesso(),
            input.criadorId()
        );

        clubeRepository.salvar(novoClube);
    }
}
