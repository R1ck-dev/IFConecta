package com.henrique.ifconecta.infrastructure.persistence.clube.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.clube.enums.StatusClube;
import com.henrique.ifconecta.domain.clube.model.Clube;
import com.henrique.ifconecta.domain.clube.port.ClubeRepository;
import com.henrique.ifconecta.domain.shared.Pagina;
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
    public boolean existePorNome(String nome) {
        return springDataClubeRepository.existsByNome(nome);
    }

    @Override
    public Pagina<Clube> listarTodosAtivos(int pagina, int tamanho) {
        // 1. Cria o objeto de paginação específico do Spring
        PageRequest pageRequest = PageRequest.of(pagina, tamanho);

        // 2. Executa a query paginada no banco
        Page<ClubeJpaEntity> pageJpa = springDataClubeRepository.findAllByStatus(StatusClube.ATIVO, pageRequest);

        // 3. Converte as entidades JPA para Domínio
        List<Clube> clubesDomain = pageJpa.getContent().stream()
                .map(clubeMapper::toDomain)
                .collect(Collectors.toList());

        // 4. Encapsula na nossa Pagina agnóstica a framework
        return new Pagina<>(
                clubesDomain,
                pageJpa.getNumber(),
                pageJpa.getTotalPages(),
                pageJpa.getTotalElements());
    }

}
