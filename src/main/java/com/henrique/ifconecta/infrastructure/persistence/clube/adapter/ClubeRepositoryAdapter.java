package com.henrique.ifconecta.infrastructure.persistence.clube.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.clube.enums.StatusClube;
import com.henrique.ifconecta.domain.clube.model.Clube;
import com.henrique.ifconecta.domain.clube.port.ClubeRepository;
import com.henrique.ifconecta.infrastructure.persistence.clube.entity.ClubeJpaEntity;
import com.henrique.ifconecta.infrastructure.persistence.clube.mapper.ClubeMapper;
import com.henrique.ifconecta.infrastructure.persistence.clube.repository.SpringDataClubeRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClubeRepositoryAdapter implements ClubeRepository {

    private final SpringDataClubeRepository springDataClubeRepository;
    private final ClubeMapper clubeMapper;

    @Override
    public Clube salvar(Clube clube) {
        ClubeJpaEntity entity = clubeMapper.toEntity(clube);

        ClubeJpaEntity savedEntity = springDataClubeRepository.save(entity);

        return clubeMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Clube> buscarPorId(UUID id) {
        return springDataClubeRepository.findById(id)
                .map(clubeMapper::toDomain);
    }

    @Override
    public List<Clube> listarTodosAtivos() {
        return springDataClubeRepository.findAllByStatus(StatusClube.ATIVO).stream()
                .map(clubeMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existePorNome(String nome) {
        return springDataClubeRepository.existsByNome(nome);
    }

}
