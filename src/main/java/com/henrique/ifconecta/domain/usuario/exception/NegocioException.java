package com.henrique.ifconecta.domain.usuario.exception;

public class NegocioException extends RuntimeException {
    public NegocioException(String message) {
        super(message);
    }
}
