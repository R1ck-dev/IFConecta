# IFConecta

Plataforma de comunicação acadêmica para o IFSP — uma rede interna onde alunos, professores e setores institucionais conversam em **clubes**, publicam **posts**, organizam **turmas** e recebem **comunicados** oficiais.

O repositório reúne dois projetos:

- **Backend** (`/`) — API REST em Spring Boot 4 + Java 21, arquitetura hexagonal, PostgreSQL.
- **Desktop** (`ifconecta-desktop/`) — cliente desktop em JavaFX 21, consumindo a API.

---

## Sumário

- [Stack](#stack)
- [Estrutura do repositório](#estrutura-do-repositório)
- [Pré-requisitos](#pré-requisitos)
- [Como rodar](#como-rodar)
- [Módulos](#módulos)
- [Arquitetura](#arquitetura)
- [Documentação e ferramentas](#documentação-e-ferramentas)

---

## Stack

**Backend**
- Java 21, Spring Boot 4.0.5
- Spring Web MVC, Spring Data JPA, Spring Security
- PostgreSQL 15 + Flyway (migrações versionadas)
- JWT stateless (jjwt 0.12.5)
- Lombok, SpringDoc OpenAPI (Swagger UI)

**Desktop**
- JavaFX 21
- Jackson (serialização JSON)
- Ikonli (ícones)
- Maven

**Infra local**
- Docker Compose (PostgreSQL + MailHog)

---

## Estrutura do repositório

```
ifconecta/
├── src/                                 # backend Spring Boot
│   ├── main/java/com/henrique/ifconecta/
│   │   ├── domain/<feature>/            # modelos, ports e enums (sem Spring/JPA)
│   │   ├── application/<feature>/       # use cases (@Service) e DTOs de entrada
│   │   └── infrastructure/
│   │       ├── web/<feature>/           # controllers REST + DTOs HTTP
│   │       ├── persistence/<feature>/   # entidades JPA, repositórios, adapters, mappers
│   │       └── config/                  # security, OpenAPI, exception handler
│   └── main/resources/db/migration/     # migrações Flyway V{n}__*.sql
├── ifconecta-desktop/                   # cliente desktop JavaFX
├── docker-compose.yml                   # Postgres + MailHog
├── IF Conecta - Otimizado.postman_collection.json
└── pom.xml
```

Cada uma das cinco features (`usuario`, `academico`, `clube`, `post`, `notificacao`) aparece nas três camadas — `domain`, `application` e `infrastructure` — sempre na mesma ordem: **Controller → UseCase → Port → Adapter → Repository → Mapper → Entity**.

---

## Pré-requisitos

- JDK 21
- Docker + Docker Compose
- Maven Wrapper já incluso (`./mvnw` ou `mvnw.cmd`)

---

## Como rodar

### 1. Suba os serviços de infra

```bash
docker compose up -d
```

Isso levanta:
- **PostgreSQL** em `localhost:5433` (banco `ifconecta_lp1`)
- **MailHog**: SMTP em `localhost:1025`, interface web em <http://localhost:8025>

> O banco desta branch é `ifconecta_lp1`, separado do `ifconecta` usado pela branch
> `release/versao-apsi` — as duas têm migrações Flyway divergentes e não compartilham banco.

### 2. Configure variáveis de ambiente

| Variável | Obrigatória | Descrição |
|---|---|---|
| `JWT_SECRET` | sim | Chave para assinar tokens JWT |
| `DB_USER` | sim | Usuário do PostgreSQL |
| `DB_PASSWORD` | sim | Senha do PostgreSQL |
| `admin.default.email` | não | Email do admin seed inicial |
| `admin.default.password` | não | Senha do admin seed inicial |

No primeiro start, o `AdminSeeder` cria um usuário `Institucional` com papel `ROLE_ADMIN` caso ainda não exista nenhum admin.

### 3. Rode o backend

```bash
./mvnw spring-boot:run        # Linux / macOS / Git Bash
mvnw.cmd spring-boot:run      # Windows PowerShell / CMD
```

A API sobe em <http://localhost:8080>.
Swagger UI: <http://localhost:8080/swagger-ui.html>.

### 4. Rode o desktop JavaFX

```bash
./mvnw -f ifconecta-desktop/pom.xml javafx:run      # Linux / macOS / Git Bash
mvnw.cmd -f ifconecta-desktop/pom.xml javafx:run    # Windows PowerShell / CMD
```

A janela do cliente abre consumindo a API em <http://localhost:8080>.
Detalhes em [ifconecta-desktop/README.md](ifconecta-desktop/README.md).

### Testes

```bash
./mvnw test                                                           # toda a suíte
./mvnw test -Dtest=IfconectaApplicationTests#contextLoads             # um teste
```

---

## Módulos

| Módulo | Responsabilidade |
|---|---|
| **usuario** | Cadastro, autenticação JWT, três subtipos (`Aluno`, `Professor`, `Institucional`), ativação por e-mail, fluxo de convite para professores/institucionais |
| **academico** | Cursos, disciplinas e turmas; aluno solicita criação de turma e professor aprova |
| **clube** | Clubes/comunidades onde alunos se reúnem por interesse |
| **post** | Publicações em clubes ou globais, com sistema de upvote (toggle) e opção anônima |
| **notificacao** | Comunicados emitidos por institucionais/professores com matriz de permissão por papel |

Domínios de e-mail aceitos no cadastro de aluno: `@aluno.ifsp.edu.br` e `@ifsp.edu.br`.

---

## Arquitetura

Hexagonal / ports-and-adapters, organizada **por feature antes da camada**. Regras que o código segue à risca:

- **Modelos de domínio não são entidades JPA.** Cada feature tem `Usuario` (domínio puro) e `UsuarioJpaEntity` (persistência), com um `*Mapper` traduzindo entre os dois.
- **Use cases dependem de ports**, nunca de Spring Data diretamente. Os adapters em `infrastructure/persistence/*/adapter` implementam essas ports.
- **Controllers só traduzem**: request DTO → input do use case → `ResponseEntity`. Validação fica nos DTOs com Jakarta Validation.
- **Schema é controlado pelo Flyway.** `ddl-auto=validate` — qualquer mudança no banco vira uma nova migração `V{n}__*.sql`.
- **Erros de negócio** sobem como `NegocioException` e são traduzidos para `400 {"erro": "..."}` pelo `GlobalExcpetHandler`.

Para quem for contribuir, o [CLAUDE.md](CLAUDE.md) tem a visão técnica detalhada.

---

## Documentação e ferramentas

- **Swagger UI**: <http://localhost:8080/swagger-ui.html>
- **MailHog**: <http://localhost:8025> (inspecionar e-mails de ativação/convite em dev)
- **Postman**: importe `IF Conecta - Otimizado.postman_collection.json` para uma coleção pronta com os endpoints principais

---

## Status

Em desenvolvimento ativo. Projeto pessoal do autor com uso pedagógico em disciplinas do IFSP — branches `release/versao-apsi` e `release/versao-lp1` mantêm cortes reduzidos do escopo para entrega acadêmica; `main` é o tronco livre.
