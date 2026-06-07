package com.henrique.ifconecta.infrastructure.persistence.usuario.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.usuario.port.EmailSenderPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JavaMailSenderAdapter implements EmailSenderPort {

    private final JavaMailSender mailSender;

    @Value("${app.web.base-url:http://localhost:8080}")
    private String webBaseUrl;

    @Value("${app.mail.from:no-reply@ifconecta.app}")
    private String remetente;

    @Override
    public void enviarEmailAtivacao(String destinatario, String nome, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(remetente);
        message.setTo(destinatario);
        message.setSubject("Ative sua conta no IFConecta");

        String urlAtivacao = webBaseUrl + "/conta/ativar.html?token=" + token;

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
        message.setFrom(remetente);
        message.setTo(destinatario);
        message.setSubject("Você foi convidado para ativar sua conta no IFConecta");

        String urlAtivacao = webBaseUrl + "/conta/ativar-convidado.html?token=" + token;

        message.setText("Olá, " + nome + "!\n\n" +
                "Bem-vindo ao IFConecta. Para começar a usar a plataforma, defina sua senha clicando no link abaixo:\n"
                +
                urlAtivacao + "\n\n" +
                "O link é válido por 24 horas.");

        mailSender.send(message);
    }

}
