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

    @Value("${app.web.base-url:http://localhost:5173}")
    private String webBaseUrl;

    @Value("${app.mail.from:no-reply@ifconecta.app}")
    private String remetente;

    @Override
    public void enviarEmailAtivacao(String destinatario, String nome, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(remetente);
        message.setTo(destinatario);
        message.setSubject("Ative sua conta no IFConecta");

        String urlAtivacao = webBaseUrl + "/ativar?token=" + token;

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

        String urlAtivacao = webBaseUrl + "/ativar-convidado?token=" + token;

        message.setText("Olá, " + nome + "!\n\n" +
                "Bem-vindo ao IFConecta. Para começar a usar a plataforma, defina sua senha clicando no link abaixo:\n"
                +
                urlAtivacao + "\n\n" +
                "O link é válido por 24 horas.");

        mailSender.send(message);
    }

    @Override
    public void enviarEmailRedefinicaoSenha(String destinatario, String nome, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(remetente);
        message.setTo(destinatario);
        message.setSubject("Redefinição de senha no IFConecta");

        String url = webBaseUrl + "/redefinir-senha?token=" + token;

        message.setText("Olá, " + nome + "!\n\n" +
                "Recebemos uma solicitação para redefinir a senha da sua conta no IFConecta. " +
                "Clique no link abaixo para escolher uma nova senha:\n\n" +
                url + "\n\n" +
                "Se você não solicitou essa redefinição, ignore este e-mail.\n" +
                "O link é válido por 1 hora.");

        mailSender.send(message);
    }

}
