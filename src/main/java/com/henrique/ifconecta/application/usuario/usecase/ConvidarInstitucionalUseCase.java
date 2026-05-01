package com.henrique.ifconecta.application.usuario.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.usuario.dto.ConvidarInstitucionalInput;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;
import com.henrique.ifconecta.domain.usuario.model.Institucional;
import com.henrique.ifconecta.domain.usuario.model.TokenVerificacao;
import com.henrique.ifconecta.domain.usuario.port.EmailSenderPort;
import com.henrique.ifconecta.domain.usuario.port.EmailValidatorPort;
import com.henrique.ifconecta.domain.usuario.port.PasswordEncoderPort;
import com.henrique.ifconecta.domain.usuario.port.TokenVerificacaoRepository;
import com.henrique.ifconecta.domain.usuario.port.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConvidarInstitucionalUseCase {

    private final UsuarioRepository usuarioRepository;
    private final EmailValidatorPort emailValidatorPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenVerificacaoRepository tokenVerificacaoRepository;
    private final EmailSenderPort emailSenderPort;

    @Transactional
    public void execute(ConvidarInstitucionalInput input) {
        if (!emailValidatorPort.isValidAcademicEmail(input.emailAcad())) {
            throw new NegocioException("O e-mail deve ser um domínio acadêmico válido.");
        }

        if (usuarioRepository.existePorEmail(input.emailAcad())) {
            throw new NegocioException("Já existe um usuário registrado com este e-mail.");
        }

        String senhaAleatoria = UUID.randomUUID().toString();
        String hash = passwordEncoderPort.encode(senhaAleatoria);

        Institucional novoInstitucional = new Institucional(
            null,
            null,
            input.nome(),
            input.emailAcad(),
            hash,
            input.setor(),
            input.cargo()
        );

        Institucional institucionalSalvo = (Institucional) usuarioRepository.salvar(novoInstitucional);

        // Gera o token de convite
        TokenVerificacao token = new TokenVerificacao(institucionalSalvo);
        tokenVerificacaoRepository.salvar(token);

        // Dispara o e-mail
        emailSenderPort.enviarEmailConvite(institucionalSalvo.getEmailAcad(), institucionalSalvo.getNome(), token.getToken());
    }
}