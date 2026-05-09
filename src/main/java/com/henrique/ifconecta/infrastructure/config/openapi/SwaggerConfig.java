package com.henrique.ifconecta.infrastructure.config.openapi;

import io.swagger.v3.oas.models.Components;
import org.springdoc.core.models.GroupedOpenApi;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi usuarioPublicoApi() {
        return GroupedOpenApi.builder()
                .group("acesso-publico")
                .pathsToMatch("/api/usuarios/alunos", "/api/usuarios/ativar", "/api/auth/login")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("IFConecta API")
                        .version("v1")
                        .description("API REST para a plataforma IFConecta, com foco no fluxo público de alunos."))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
