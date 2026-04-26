package com.henrique.ifconecta.infrastructure.persistence.post.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.post.model.Post;
import com.henrique.ifconecta.domain.post.port.PostRepository;
import com.henrique.ifconecta.infrastructure.persistence.post.entity.PostJpaEntity;
import com.henrique.ifconecta.infrastructure.persistence.post.mapper.PostMapper;
import com.henrique.ifconecta.infrastructure.persistence.post.repository.SpringDataPostRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostRepositoryAdapter implements PostRepository {

    private final SpringDataPostRepository springDataPostRepository;
    private final PostMapper postMapper;

    @Override
    public Post salvar(Post post) {
        PostJpaEntity entity = postMapper.toEntity(post);
        PostJpaEntity savedEntity = springDataPostRepository.save(entity);
        return postMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Post> buscarPorId(UUID id) {
        return springDataPostRepository.findById(id).map(postMapper::toDomain);
    }
}
