package com.henrique.ifconecta.infrastructure.persistence.usuario.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.usuario.model.TokenVerificacao;
import com.henrique.ifconecta.domain.usuario.port.TokenVerificacaoRepository;
import com.henrique.ifconecta.infrastructure.persistence.usuario.entity.TokenVerificacaoJpaEntity;
import com.henrique.ifconecta.infrastructure.persistence.usuario.mapper.TokenVerificacaoMapper;
import com.henrique.ifconecta.infrastructure.persistence.usuario.repository.SpringDataTokenVerificacaoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenVerificacaoRepositoryAdapter implements TokenVerificacaoRepository {

    private final SpringDataTokenVerificacaoRepository jpaRepository;
    private final TokenVerificacaoMapper mapper;

    @Override
    public TokenVerificacao salvar(TokenVerificacao token) {
        TokenVerificacaoJpaEntity entity = mapper.toEntity(token);

        TokenVerificacaoJpaEntity savedEntity = jpaRepository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<TokenVerificacao> buscarPorToken(String token) {
        Optional<TokenVerificacaoJpaEntity> entity = jpaRepository.findByToken(token);

        return entity.map(mapper::toDomain);
    }

}
