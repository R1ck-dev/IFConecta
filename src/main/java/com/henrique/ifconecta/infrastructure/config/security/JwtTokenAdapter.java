package com.henrique.ifconecta.infrastructure.config.security;

import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.usuario.enums.RoleUsuario;
import com.henrique.ifconecta.domain.usuario.model.Usuario;
import com.henrique.ifconecta.domain.usuario.port.TokenServicePort;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenAdapter implements TokenServicePort {

    @Value("${api.security.jwt.secret}")
    private String secret;

    @Value("${api.security.jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    public String gerarToken(Usuario usuario) {
        SecretKey key = getSecretKey();

        var jwtBuilder = Jwts.builder()
                .subject(usuario.getId().toString())
                .claim("id", usuario.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs));

        if (RoleUsuario.ADMIN.equals(usuario.getRole())) {
            jwtBuilder.claim("role", "ROLE_ADMIN");
        }

        return jwtBuilder
                .signWith(key)
                .compact();
    }

    @Override
    public String obterIdDoUsuario(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("id", String.class);
    }

    @Override
    public Optional<String> obterRoleDoUsuario(String token) {
        String role = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);

        return Optional.ofNullable(role);
    }
}
