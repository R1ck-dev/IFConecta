package com.henrique.ifconecta.application.usuario.usecase;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.usuario.dto.RegistrarAlunoInput;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;
import com.henrique.ifconecta.domain.usuario.model.Aluno;
import com.henrique.ifconecta.domain.usuario.port.EmailValidatorPort;
import com.henrique.ifconecta.domain.usuario.port.PasswordEncoderPort;
import com.henrique.ifconecta.domain.usuario.port.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistrarAlunoUseCase {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoderPort passwordEncoderPort;
    private final EmailValidatorPort emailValidatorPort;

    @Transactional
    public void execute(RegistrarAlunoInput input) {
        if (!emailValidatorPort.isValidAcademicEmail(input.email())) {
            throw new NegocioException("O e-mail deve ser um domínio acadêmico válido (ex: @aluno.ifsp.edu.br).");
        }

        if (usuarioRepository.existePorEmail(input.email())) {
            throw new NegocioException("Já existe um utilizador registrado com este endereço de e-mail.");
        }

        String hash = passwordEncoderPort.encode(input.password());

         Aluno novoAluno = new Aluno(
            null,
            input.nome(), 
            input.email(), 
            hash, 
            input.prontuario()
        );

        usuarioRepository.salvar(novoAluno);

        // TODO: Disparar evento de envio de e-mail de confirmação.
    }
}
