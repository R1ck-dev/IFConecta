# IFConecta Desktop (JavaFX)

Cliente **desktop** do IFConecta, escrito em **JavaFX 21**. Substitui o frontend web
(`ifconecta-web`) para execução como aplicação de plataforma, consumindo a mesma API
REST do backend Spring Boot.

> Migração **faseada**. Esta é a **Fase A — núcleo**: fundação técnica + Login + AppShell
> (header/sidebar) + Timeline, integrados ao backend. As demais telas e modais vêm na Fase B.

## Stack

- **JavaFX 21** — UI em FXML + CSS (o CSS é um port dos design tokens do front web).
- **java.net.http.HttpClient** — chamadas REST (`core/http/ApiClient`).
- **Jackson** — JSON ⇄ records (`model/`).
- **Ikonli Feather** — ícones outline.
- **Maven** — build (`mvn javafx:run`).

## Pré-requisitos

- JDK 21
- Backend IFConecta no ar (ver README da raiz): PostgreSQL via `docker compose up -d`
  e a API com `./mvnw spring-boot:run` (porta `8080`).

## Como rodar

A partir da **raiz do repositório** (usa o Maven Wrapper do projeto):

```bash
mvnw.cmd -f ifconecta-desktop/pom.xml javafx:run
```

A URL da API é `http://localhost:8080` por padrão. Para apontar para outro ambiente,
defina a variável de ambiente `IFCONECTA_API_URL` antes de iniciar.

## Credenciais de teste

A conta administradora padrão é criada pelo backend (`AdminSeeder`):

- **Email:** `admin@ifsp.edu.br`
- **Senha:** `SenhaForte123!`

## Estrutura

```
src/main/java/com/henrique/ifconecta/desktop/
├── IFConectaApp.java        Application — monta a cena, tema, roteador
├── core/                    Router, Session, AsyncRunner, http/ApiClient
├── model/                   records espelhando os DTOs do backend
├── service/                 AuthService, CursoService, PostService
├── controller/              LoginController, AppShellController, TimelineController
└── ui/                      Theme, Toast, Avatar, Icons, Format
src/main/resources/com/henrique/ifconecta/desktop/
├── view/                    *.fxml
└── css/                     tokens.css, tokens-dark.css, app.css
```

## Mapa Web → Desktop

| Front web (React) | Desktop (JavaFX) |
|---|---|
| React Router | `core/Router` |
| `axios` + interceptors | `core/http/ApiClient` |
| `AuthContext` / `localStorage` | `core/Session` |
| `useToast` | `ui/Toast` |
| design tokens CSS | `css/tokens*.css` (oklch → sRGB) |
| `async/await` + loading | `core/AsyncRunner` (`Task`) |
