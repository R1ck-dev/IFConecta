package com.henrique.ifconecta.infrastructure.config.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.usuario.model.Usuario;
import com.henrique.ifconecta.domain.usuario.port.TokenServicePort;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenAdapter implements TokenServicePort{

    @Value("${api.security.jwt.secret}")
    private String secret;

    @Value("${api.security.jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    public String gerarToken(Usuario usuario) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.builder()
                .subject(usuario.getEmailAcad())
                .claim("id", usuario.getId().toString())
                .claim("role", "ROLE_" + usuario.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    @Override
    public String obterIdDoUsuario(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey()) // Valida a assinatura
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("id", String.class); // Busca a claim customizada de ID
    }

    @Override
    public String obterRoleDoUsuario(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class); // Busca a claim de role
    }
    
}
