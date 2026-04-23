package com.henrique.ifconecta.infrastructure.config.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.henrique.ifconecta.domain.usuario.port.TokenServicePort;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{
    
    private final TokenServicePort tokenServicePort;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = recuperarToken(request);

        if (token != null) {
            try {
                // Se a assinatura for inválida ou estiver expirado, o Adapter lança exceção
                String subjectId = tokenServicePort.obterIdDoUsuario(token);
                String role = tokenServicePort.obterRoleDoUsuario(token);

                // Converte a String de Role para a classe de Autoridade do Spring
                var authorities = List.of(new SimpleGrantedAuthority(role));

                // Avisa ao Spring que a autenticação foi concluída e passa o id e permissões
                var authentication = new UsernamePasswordAuthenticationToken(subjectId, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {}
        }

        filterChain.doFilter(request, response);

    }

    private String recuperarToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        return authHeader.replace("Bearer ", "");
    }

    
}
