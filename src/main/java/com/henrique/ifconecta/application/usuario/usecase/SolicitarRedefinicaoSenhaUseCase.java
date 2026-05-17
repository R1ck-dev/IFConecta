package com.henrique.ifconecta.application.usuario.usecase;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.usuario.dto.SolicitarRedefinicaoSenhaInput;
import com.henrique.ifconecta.domain.usuario.enums.StatusUsuario;
import com.henrique.ifconecta.domain.usuario.enums.TipoToken;
import com.henrique.ifconecta.domain.usuario.model.TokenVerificacao;
import com.henrique.ifconecta.domain.usuario.model.Usuario;
import com.henrique.ifconecta.domain.usuario.port.EmailSenderPort;
import com.henrique.ifconecta.domain.usuario.port.TokenVerificacaoRepository;
import com.henrique.ifconecta.domain.usuario.port.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SolicitarRedefinicaoSenhaUseCase {

    private final UsuarioRepository usuarioRepository;
    private final TokenVerificacaoRepository tokenVerificacaoRepository;
    private final EmailSenderPort emailSenderPort;

    @Transactional
    public void execute(SolicitarRedefinicaoSenhaInput input) {
        if (input.email() == null || input.email().isBlank()) {
            return;
        }

        Usuario usuario = usuarioRepository.buscarPorEmail(input.email().trim()).orElse(null);

        // Sempre retorna sucesso para não revelar se o e-mail existe.
        if (usuario == null || usuario.getStatus() != StatusUsuario.ATIVO) {
            return;
        }

        TokenVerificacao token = new TokenVerificacao(usuario, TipoToken.REDEFINICAO_SENHA);
        tokenVerificacaoRepository.salvar(token);

        emailSenderPort.enviarEmailRedefinicaoSenha(
                usuario.getEmailAcad(), usuario.getNome(), token.getToken());
    }
}
