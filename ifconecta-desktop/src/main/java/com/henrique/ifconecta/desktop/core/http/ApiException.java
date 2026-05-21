package com.henrique.ifconecta.desktop.core.http;

/** Erro de chamada à API, com o status HTTP e uma mensagem amigável. */
public class ApiException extends RuntimeException {

    private final int status;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    /** Status HTTP; 0 indica falha de rede/conexão. */
    public int status() {
        return status;
    }

    public boolean isUnauthorized() {
        return status == 401;
    }

    public boolean isNotFound() {
        return status == 404;
    }
}
