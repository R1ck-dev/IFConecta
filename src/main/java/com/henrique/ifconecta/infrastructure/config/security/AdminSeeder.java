package com.henrique.ifconecta.infrastructure.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.usuario.model.Institucional;
import com.henrique.ifconecta.domain.usuario.port.PasswordEncoderPort;
import com.henrique.ifconecta.domain.usuario.port.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoderPort passwordEncoder;

    @Value("${admin.default.email:admin@ifsp.edu.br}")
    private String adminEmail;

    @Value("${admin.default.password:SenhaForte123!}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        
        log.info("Iniciando verificação de conta Administradora padrão...");

        if (!usuarioRepository.existePorEmail(adminEmail)) {
            log.info("Conta Admin '{}' não encontrada. Criando...", adminEmail);

            String hash = passwordEncoder.encode(adminPassword);

            Institucional admin = new Institucional(
                    null, 
                    null, 
                    "Administrador IFConecta", 
                    adminEmail, 
                    hash, 
                    "TI", 
                    "Super Admin"
            );

            admin.promoverParaAdmin();
            admin.ativarConta(); 

            usuarioRepository.salvar(admin);

            log.info("Conta Administradora padrão criada com sucesso!");
        } else {
            log.info("Conta Administradora padrão já existe. Nenhuma ação necessária.");
        }
    }
}