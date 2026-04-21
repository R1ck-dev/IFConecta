package com.henrique.ifconecta.domain.usuario.port;

import java.util.Optional;
import java.util.UUID;

import com.henrique.ifconecta.domain.usuario.model.Usuario;

public interface UsuarioRepository {
    Usuario salvar(Usuario usuario);
    Optional<Usuario> buscarPorId(UUID id);
    Optional<Usuario> buscarPorEmail(String email);
    boolean existePorEmail(String email);
} 
