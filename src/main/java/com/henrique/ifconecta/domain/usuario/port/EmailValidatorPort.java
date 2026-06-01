package com.henrique.ifconecta.domain.usuario.port;

public interface EmailValidatorPort { // Adapter: AcademicEmailValidatorAdapter (infrastructure/persistence/usuario/adapter)
    boolean isValidAcademicEmail(String email);    
}
