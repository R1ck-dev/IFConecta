package com.henrique.ifconecta.infrastructure.persistence.usuario.adapter;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.usuario.port.EmailSenderPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JavaMailSenderAdapter implements EmailSenderPort {

    private final JavaMailSender mailSender;

    @Override
    public void enviarEmailAtivacao(String destinatario, String nome, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("Ative sua conta no IFConecta");

        // TODO: Passar essa URL para uma variável de ambiente
        String urlAtivacao = "http://localhost:8080/api/usuarios/ativar?token=" + token;

        message.setText("Olá, " + nome + "!\n\n" +
                "Bem-vindo ao IFConecta. Para começar a usar a plataforma, confirme seu e-mail clicando no link abaixo:\n"
                +
                urlAtivacao + "\n\n" +
                "O link é válido por 24 horas.");

        mailSender.send(message);
    }

    @Override
    public void enviarEmailConvite(String destinatario, String nome, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("Você foi convidado para ativar sua conta no IFConecta");

        // TODO: Passar essa URL para uma variável de ambiente
        String urlAtivacao = "http://localhost:8080/api/usuarios/definir-senha?token=" + token;

        message.setText("Olá, " + nome + "!\n\n" +
                "Bem-vindo ao IFConecta. Para começar a usar a plataforma, confirme seu e-mail clicando no link abaixo:\n"
                +
                urlAtivacao + "\n\n" +
                "O link é válido por 24 horas.");

        mailSender.send(message);
    }

}
