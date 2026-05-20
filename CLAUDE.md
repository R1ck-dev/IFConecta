# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Stack

Spring Boot 4.0.5 on Java 21, PostgreSQL via JPA/Hibernate, Flyway for migrations, Spring Security with JWT (jjwt 0.12.5), Lombok, SpringDoc OpenAPI (Swagger UI). Code, package names, and domain language are in Portuguese (pt-BR).

## Common commands

The project ships the Maven wrapper. On Windows use `mvnw.cmd`; the bash examples below work in Git Bash / WSL.

```bash
./mvnw spring-boot:run                    # run the app (profile=local is the default)
./mvnw test                               # run the full test suite
./mvnw test -Dtest=IfconectaApplicationTests#contextLoads   # run a single test
./mvnw clean package                      # build the jar (skip -DskipTests to skip tests)
docker compose up -d                      # start Postgres + Mailhog
```

Required env vars at runtime: `JWT_SECRET` (mandatory — `application.yml` has no default), `DB_USER`, `DB_PASSWORD`. Optional: `admin.default.email`, `admin.default.password` (read by [AdminSeeder.java](src/main/java/com/henrique/ifconecta/infrastructure/config/security/AdminSeeder.java)).

Local dev services:
- Postgres in `docker-compose.yml` exposes **5432**, but `application-local.yml` connects to **5433**. Reconcile one of the two before first run (or override `spring.datasource.url`).
- Mailhog SMTP on `localhost:1025`, web UI on `http://localhost:8025`.
- Swagger UI at `http://localhost:8080/swagger-ui.html`.

`spring.jpa.hibernate.ddl-auto=validate` — schema changes must go through a new Flyway migration in [src/main/resources/db/migration/](src/main/resources/db/migration/) (`V{n}__...sql`); Hibernate will refuse to start if entities drift from the schema.

## Architecture

Hexagonal / ports-and-adapters, organized **by feature first, then by layer**. The five feature modules (`academico`, `clube`, `notificacao`, `post`, `usuario`) each appear inside three top-level packages:

```
com.henrique.ifconecta
├── domain.<feature>              # pure Java — no Spring, no JPA
│   ├── model/                    # rich domain objects with behavior
│   ├── port/                     # interfaces the application layer depends on
│   └── enums/                    # domain enums
├── application.<feature>
│   ├── usecase/                  # @Service classes, one public execute() per use case
│   └── dto/                      # input records + summary DTOs returned to controllers
└── infrastructure
    ├── web.<feature>             # REST controllers + request/response DTOs
    ├── persistence.<feature>     # JPA entities + Spring Data repos + adapters + mappers
    └── config/                   # security, OpenAPI, global exception handling
```

The flow is always: **Controller → UseCase → domain Port → Adapter → SpringData repo → Mapper → JPA entity**. Cross those boundaries deliberately:

- **Domain models are not JPA entities.** Domain classes like [Usuario.java](src/main/java/com/henrique/ifconecta/domain/usuario/model/Usuario.java) live in `domain.*.model` with constructors for *creation* vs *reconstitution* (the latter takes pre-existing id, status, timestamps). JPA entities like `UsuarioJpaEntity` live in `infrastructure.persistence.*.entity`. A mapper in `infrastructure.persistence.*.mapper` translates between the two — see [UsuarioMapper.java](src/main/java/com/henrique/ifconecta/infrastructure/persistence/usuario/mapper/UsuarioMapper.java), which calls `Hibernate.unproxy(entity)` before `instanceof` checks because `Usuario` uses JOINED inheritance (`Aluno`, `Professor`, `Institucional`).
- **Ports are domain interfaces; adapters are Spring beans.** A use case depends on `UsuarioRepository` (port), not on `SpringDataUsuarioRepository`. The adapter ([UsuarioRepositoryAdapter.java](src/main/java/com/henrique/ifconecta/infrastructure/persistence/usuario/adapter/UsuarioRepositoryAdapter.java)) implements the port and delegates to Spring Data. Same pattern for `PasswordEncoderPort`, `TokenServicePort`, `EmailSenderPort`, `EmailValidatorPort`, `AuthenticationPort` — all implemented under `infrastructure.config.security` or `infrastructure.persistence.usuario.adapter`.
- **Controllers do nothing but translate.** They map a request DTO → use-case input record, call `execute(...)`, and return a `ResponseEntity`. Validation lives on the request DTO (`@Valid` + Jakarta constraints).

When adding a new feature, mirror this five-way split. When adding to an existing feature, place new code in the layer that matches its responsibility — never let `infrastructure` types leak into `domain` or `application`.

## Auth & users

- JWT stateless auth, filter [JwtAuthenticationFilter.java](src/main/java/com/henrique/ifconecta/infrastructure/config/security/JwtAuthenticationFilter.java) wired before `UsernamePasswordAuthenticationFilter`.
- Public endpoints (see [SecurityConfig.java](src/main/java/com/henrique/ifconecta/infrastructure/config/security/SecurityConfig.java)): `POST /api/usuarios/alunos`, `POST /api/auth/login`, `GET /api/usuarios/ativar`, `POST /api/usuarios/ativar-convidado`, Swagger UI. Everything under `/api/admin/**` requires `ROLE_ADMIN`; everything else requires authentication.
- Three user subtypes share table `usuarios` via JOINED inheritance: `Aluno` (prontuario), `Professor` (siape), `Institucional` (setor/cargo). Self-registration is only for Aluno; Professor/Institucional are *invited* by an admin and activate via a token + password-set flow.
- Academic emails are restricted to `@aluno.ifsp.edu.br` and `@ifsp.edu.br` by [AcademicEmailValidatorAdapter.java](src/main/java/com/henrique/ifconecta/infrastructure/persistence/usuario/adapter/AcademicEmailValidatorAdapter.java).
- [AdminSeeder.java](src/main/java/com/henrique/ifconecta/infrastructure/config/security/AdminSeeder.java) runs on startup and creates a default admin Institucional if none exists.

## Errors

Throw `com.henrique.ifconecta.domain.usuario.exception.NegocioException` for business-rule violations. [GlobalExcpetHandler.java](src/main/java/com/henrique/ifconecta/infrastructure/config/excpetion/GlobalExcpetHandler.java) (note the typo in the package/class name — keep using it as-is until renamed) maps it to `400 {"erro": "..."}` and also handles `MethodArgumentNotValidException` from `@Valid`.
