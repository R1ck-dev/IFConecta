package com.henrique.ifconecta.desktop;

import com.henrique.ifconecta.desktop.model.MeuPerfil;

/**
 * Guarda quem esta logado enquanto o programa esta aberto.
 *
 * Sao apenas duas variaveis: o token (a "chave" que o servidor deu no login)
 * e o perfil do usuario. Quando o usuario sai, as duas voltam a ser null.
 */
public class Sessao {

    /** Token JWT recebido no login. A classe Api envia ele em cada pedido. */
    public static String token;

    /** Dados do usuario logado (nome, tipo, se e admin, etc.). */
    public static MeuPerfil usuario;
}
