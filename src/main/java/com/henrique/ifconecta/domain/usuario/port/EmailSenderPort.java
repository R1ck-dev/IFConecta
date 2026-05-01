package com.henrique.ifconecta.domain.usuario.port;

public interface EmailSenderPort {
    void enviarEmailAtivacao(String destinatario, String nome, String token);
    void enviarEmailConvite(String destinatario, String nome, String token);
}