package com.henrique.ifconecta.infrastructure.persistence.usuario.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.usuario.model.Usuario;
import com.henrique.ifconecta.domain.usuario.port.UsuarioRepository;
import com.henrique.ifconecta.infrastructure.persistence.usuario.entity.UsuarioJpaEntity;
import com.henrique.ifconecta.infrastructure.persistence.usuario.mapper.UsuarioMapper;
import com.henrique.ifconecta.infrastructure.persistence.usuario.repository.SpringDataUsuarioRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
// Cria automaticamente um construtor com todos os atributos marcados com Final.
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final SpringDataUsuarioRepository springDataUsuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    public Usuario salvar(Usuario usuario) {
        UsuarioJpaEntity entity = usuarioMapper.toEntity(usuario);
        UsuarioJpaEntity savedEntity = springDataUsuarioRepository.save(entity);
        return usuarioMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Usuario> buscarPorId(UUID id) {
        return springDataUsuarioRepository.findById(id).map(usuarioMapper::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return springDataUsuarioRepository.findByEmailAcad(email).map(usuarioMapper::toDomain);
    }

    @Override
    public boolean existePorEmail(String email) {
        return springDataUsuarioRepository.existsByEmailAcad(email);
    }

    @Override
    public List<UUID> buscarTodosIdsAtivos() {
        return springDataUsuarioRepository.findAllAtivosIds();
    }

    @Override
    public List<UUID> buscarIdsPorCurso(UUID cursoId) {
        return springDataUsuarioRepository.findIdsByCursoId(cursoId);
    }

}
