package com.henrique.ifconecta.desktop.core;

import com.henrique.ifconecta.desktop.model.MeuPerfil;

/**
 * Estado de autenticação em memória — equivale ao AuthContext do front web.
 * Guarda o token JWT e o perfil do usuário logado durante a sessão.
 */
public final class Session {

    private static final Session INSTANCE = new Session();

    private String token;
    private MeuPerfil me;

    private Session() {
    }

    public static Session get() {
        return INSTANCE;
    }

    public String token() {
        return token;
    }

    public MeuPerfil me() {
        return me;
    }

    public boolean authenticated() {
        return token != null && me != null;
    }

    public void set(String token, MeuPerfil me) {
        this.token = token;
        this.me = me;
    }

    public void clear() {
        this.token = null;
        this.me = null;
    }
}
