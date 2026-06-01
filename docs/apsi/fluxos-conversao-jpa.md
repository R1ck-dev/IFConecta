# Fluxos de casos de uso com conversão e JPA Entity

Diagramas dos fluxos **CriarClube** e **CriarPost** acrescentando o passo de conversão
domínio ↔ entidade (`Mapper`) e as classes `JpaEntity`. As assinaturas de métodos e os
campos refletem o código atual.

## Fluxo 1 — CriarClube

```mermaid
classDiagram
    direction LR

    class CriarClubeRequest {
        <<record>>
        +nome : String
        +descricao : String
    }

    class CriarClubeInput {
        <<record>>
        +nome : String
        +descricao : String
        +criadorId : UUID
    }

    class ClubeController {
        +criarClube(request : CriarClubeRequest) : void
        -extraiId() : String
    }

    class CriarClubeUseCase {
        +execute(input : CriarClubeInput) : void
    }

    class ClubeRepository {
        <<interface>>
        +existePorNome(nome : String) : boolean
        +salvar(clube : Clube) : Clube
    }

    class Clube
    class MembroClube
    class PapelMembro {
        <<enumeration>>
    }
    class StatusClube {
        <<enumeration>>
    }

    %% ---- Passo de conversão (NOVO) ----
    class ClubeRepositoryAdapter {
        +salvar(clube : Clube) : Clube
        +existePorNome(nome : String) : boolean
        +buscarPorId(id : UUID) : Optional~Clube~
    }

    class SpringDataClubeRepository {
        <<interface>>
        +save(entity : ClubeJpaEntity) : ClubeJpaEntity
        +existsByNome(nome : String) : boolean
    }

    class ClubeMapper {
        +toEntity(domain : Clube) : ClubeJpaEntity
        +toDomain(entity : ClubeJpaEntity) : Clube
        +toMembroEntity(domain : MembroClube, clube : ClubeJpaEntity) : MembroClubeJpaEntity
    }

    class ClubeJpaEntity {
        <<Entity>>
        -id : UUID
        -nome : String
        -descricao : String
        -status : StatusClube
        -dataCriacao : LocalDateTime
        -membros : List~MembroClubeJpaEntity~
    }

    class MembroClubeJpaEntity {
        <<Entity>>
        -id : UUID
        -papel : PapelMembro
        -dataIngresso : LocalDateTime
    }

    %% ---- Relações originais ----
    ClubeController ..> CriarClubeRequest
    ClubeController ..> CriarClubeInput
    ClubeController ..> CriarClubeUseCase : - criarClubeUseCase
    CriarClubeUseCase ..> CriarClubeInput
    CriarClubeUseCase ..> ClubeRepository : - clubeRepository
    CriarClubeUseCase ..> Clube
    Clube "1" *-- "1" StatusClube : - status
    Clube "1" *-- "*" MembroClube : - membro
    MembroClube "1" --> PapelMembro : - papel

    %% ---- Relações da conversão (NOVO) ----
    ClubeRepositoryAdapter ..|> ClubeRepository : implementa
    ClubeRepositoryAdapter ..> ClubeMapper : - clubeMapper
    ClubeRepositoryAdapter ..> SpringDataClubeRepository : - springDataClubeRepository
    ClubeMapper ..> ClubeJpaEntity : cria / lê
    ClubeMapper ..> Clube : reconstrói
    SpringDataClubeRepository ..> ClubeJpaEntity : persiste
    ClubeJpaEntity "1" *-- "*" MembroClubeJpaEntity : - membros
```

## Fluxo 2 — CriarPost

```mermaid
classDiagram
    direction LR

    class CriarPostRequest {
        <<record>>
        +conteudo : String
        +clubeId : UUID
    }

    class CriarPostInput {
        <<record>>
        +autorId : UUID
        +clubeId : UUID
        +conteudo : String
    }

    class PostController {
        +criarPost(request : CriarPostRequest) : ResponseEntity
        -extraiId() : String
    }

    class CriarPostUseCase {
        +execute(input : CriarPostInput) : void
    }

    class PostRepository {
        <<interface>>
        +salvar(post : Post) : Post
        +buscarPorId(id : UUID) : Optional~Post~
    }

    class ClubeRepository {
        <<interface>>
        +buscarPorId(id : UUID) : Optional~Clube~
    }

    class Post
    class Comentario

    %% ---- Passo de conversão (NOVO) ----
    class PostRepositoryAdapter {
        +salvar(post : Post) : Post
        +buscarPorId(id : UUID) : Optional~Post~
    }

    class SpringDataPostRepository {
        <<interface>>
        +save(entity : PostJpaEntity) : PostJpaEntity
        +findById(id : UUID) : Optional~PostJpaEntity~
    }

    class PostMapper {
        +toEntity(domain : Post) : PostJpaEntity
        +toDomain(entity : PostJpaEntity) : Post
    }

    class PostJpaEntity {
        <<Entity>>
        -id : UUID
        -autor : UsuarioJpaEntity
        -clube : ClubeJpaEntity
        -conteudo : String
        -dataCriacao : LocalDateTime
        -comentarios : List~ComentarioJpaEntity~
    }

    class ComentarioJpaEntity {
        <<Entity>>
        -id : UUID
        -autor : UsuarioJpaEntity
        -conteudo : String
        -dataCriacao : LocalDateTime
    }

    %% ---- Relações originais ----
    PostController ..> CriarPostRequest
    PostController ..> CriarPostInput
    PostController ..> CriarPostUseCase : - criarPostUseCase
    CriarPostUseCase ..> CriarPostInput
    CriarPostUseCase ..> PostRepository : - postRepository
    CriarPostUseCase ..> ClubeRepository : - clubeRepository
    CriarPostUseCase ..> Post
    Post "1" *-- "*" Comentario : - comentarios

    %% ---- Relações da conversão (NOVO) ----
    PostRepositoryAdapter ..|> PostRepository : implementa
    PostRepositoryAdapter ..> PostMapper : - postMapper
    PostRepositoryAdapter ..> SpringDataPostRepository : - springDataPostRepository
    PostMapper ..> PostJpaEntity : cria / lê
    PostMapper ..> Post : reconstrói
    SpringDataPostRepository ..> PostJpaEntity : persiste
    PostJpaEntity "1" *-- "*" ComentarioJpaEntity : - comentarios
```

## Fluxo 3 — RegistrarAluno

```mermaid
classDiagram
    direction LR

    class RegistrarAlunoRequest {
        <<record>>
        +nome : String
        +email : String
        +password : String
        +prontuario : String
    }

    class RegistrarAlunoInput {
        <<record>>
        +nome : String
        +email : String
        +password : String
        +prontuario : String
    }

    class UsuarioController {
        +registrarAluno(request : RegistrarAlunoRequest) : void
        +ativarConta(token : String) : void
    }

    class RegistrarAlunoUseCase {
        +execute(input : RegistrarAlunoInput) : void
    }

    class UsuarioRepository {
        <<interface>>
        +salvar(usuario : Usuario) : Usuario
        +existePorEmail(email : String) : boolean
        +existePorProntuario(prontuario : String) : boolean
    }
    class PasswordEncoderPort {
        <<interface>>
        +encode(rawPassword : String) : String
    }
    class EmailValidatorPort {
        <<interface>>
        +isValidAcademicEmail(email : String) : boolean
    }
    class TokenVerificacaoRepository {
        <<interface>>
        +salvar(token : TokenVerificacao) : TokenVerificacao
    }
    class EmailSenderPort {
        <<interface>>
        +enviarEmailAtivacao(destinatario : String, nome : String, token : String) : void
    }

    class Usuario
    class TokenVerificacao

    %% ---- Adapters + conversão (NOVO) ----
    class UsuarioRepositoryAdapter {
        +salvar(usuario : Usuario) : Usuario
    }
    class TokenVerificacaoRepositoryAdapter {
        +salvar(token : TokenVerificacao) : TokenVerificacao
    }
    class BCryptPasswordEncoderAdapter
    class AcademicEmailValidatorAdapter
    class JavaMailSenderAdapter

    class SpringDataUsuarioRepository {
        <<interface>>
        +save(entity : UsuarioJpaEntity) : UsuarioJpaEntity
        +existsByEmailAcad(email : String) : boolean
        +existsByProntuario(prontuario : String) : boolean
    }
    class SpringDataTokenVerificacaoRepository {
        <<interface>>
        +save(entity : TokenVerificacaoJpaEntity) : TokenVerificacaoJpaEntity
    }

    class UsuarioMapper {
        +toEntity(usuario : Usuario) : UsuarioJpaEntity
        +toDomain(entity : UsuarioJpaEntity) : Usuario
    }
    class TokenVerificacaoMapper {
        +toEntity(domain : TokenVerificacao) : TokenVerificacaoJpaEntity
        +toDomain(entity : TokenVerificacaoJpaEntity) : TokenVerificacao
    }

    class UsuarioJpaEntity {
        <<Entity>>
        -id : UUID
        -nome : String
        -emailAcad : String
        -senhaHash : String
        -status : StatusUsuario
        -dataCriacao : LocalDateTime
        -prontuario : String
    }
    class TokenVerificacaoJpaEntity {
        <<Entity>>
        -id : UUID
        -usuario : UsuarioJpaEntity
        -token : String
        -dataExpiracao : LocalDateTime
        -utilizado : boolean
    }

    %% ---- Relações ----
    UsuarioController ..> RegistrarAlunoRequest
    UsuarioController ..> RegistrarAlunoInput
    UsuarioController ..> RegistrarAlunoUseCase : - registrarAlunoUseCase
    RegistrarAlunoUseCase ..> RegistrarAlunoInput
    RegistrarAlunoUseCase ..> UsuarioRepository : - usuarioRepository
    RegistrarAlunoUseCase ..> PasswordEncoderPort : - passwordEncoderPort
    RegistrarAlunoUseCase ..> EmailValidatorPort : - emailValidatorPort
    RegistrarAlunoUseCase ..> TokenVerificacaoRepository : - tokenVerificacaoRepository
    RegistrarAlunoUseCase ..> EmailSenderPort : - emailSenderPort
    RegistrarAlunoUseCase ..> Usuario
    RegistrarAlunoUseCase ..> TokenVerificacao

    %% realizações dos ports
    UsuarioRepositoryAdapter ..|> UsuarioRepository
    TokenVerificacaoRepositoryAdapter ..|> TokenVerificacaoRepository
    BCryptPasswordEncoderAdapter ..|> PasswordEncoderPort
    AcademicEmailValidatorAdapter ..|> EmailValidatorPort
    JavaMailSenderAdapter ..|> EmailSenderPort

    %% conversão domínio <-> entidade
    UsuarioRepositoryAdapter ..> UsuarioMapper
    UsuarioRepositoryAdapter ..> SpringDataUsuarioRepository
    TokenVerificacaoRepositoryAdapter ..> TokenVerificacaoMapper
    TokenVerificacaoRepositoryAdapter ..> SpringDataTokenVerificacaoRepository
    TokenVerificacaoMapper ..> UsuarioMapper : reusa
    UsuarioMapper ..> UsuarioJpaEntity : cria / lê
    TokenVerificacaoMapper ..> TokenVerificacaoJpaEntity : cria / lê
    SpringDataUsuarioRepository ..> UsuarioJpaEntity : persiste
    SpringDataTokenVerificacaoRepository ..> TokenVerificacaoJpaEntity : persiste
    TokenVerificacaoJpaEntity "1" --> "1" UsuarioJpaEntity : - usuario
```

## Fluxo 4 — AtivarConta

```mermaid
classDiagram
    direction LR

    class UsuarioController {
        +ativarConta(token : String) : void
    }

    class AtivarContaUseCase {
        +execute(token : String) : void
    }

    class TokenVerificacaoRepository {
        <<interface>>
        +buscarPorToken(token : String) : Optional~TokenVerificacao~
        +salvar(token : TokenVerificacao) : TokenVerificacao
    }
    class UsuarioRepository {
        <<interface>>
        +salvar(usuario : Usuario) : Usuario
    }

    class TokenVerificacao {
        +validar() : void
        +marcarComoUtilizado() : void
        +getUsuario() : Usuario
    }
    class Usuario {
        +ativarConta() : void
    }

    %% ---- Adapters + conversão (NOVO) ----
    class TokenVerificacaoRepositoryAdapter
    class UsuarioRepositoryAdapter

    class SpringDataTokenVerificacaoRepository {
        <<interface>>
        +findByToken(token : String) : Optional~TokenVerificacaoJpaEntity~
        +save(entity : TokenVerificacaoJpaEntity) : TokenVerificacaoJpaEntity
    }
    class SpringDataUsuarioRepository {
        <<interface>>
        +save(entity : UsuarioJpaEntity) : UsuarioJpaEntity
    }

    class TokenVerificacaoMapper {
        +toEntity(domain : TokenVerificacao) : TokenVerificacaoJpaEntity
        +toDomain(entity : TokenVerificacaoJpaEntity) : TokenVerificacao
    }
    class UsuarioMapper {
        +toEntity(usuario : Usuario) : UsuarioJpaEntity
        +toDomain(entity : UsuarioJpaEntity) : Usuario
    }

    class TokenVerificacaoJpaEntity {
        <<Entity>>
    }
    class UsuarioJpaEntity {
        <<Entity>>
    }

    %% ---- Relações ----
    UsuarioController ..> AtivarContaUseCase : - ativarContaUseCase
    AtivarContaUseCase ..> TokenVerificacaoRepository : - tokenVerificacaoRepository
    AtivarContaUseCase ..> UsuarioRepository : - usuarioRepository
    AtivarContaUseCase ..> TokenVerificacao
    AtivarContaUseCase ..> Usuario
    TokenVerificacao "1" --> "1" Usuario : - usuario

    TokenVerificacaoRepositoryAdapter ..|> TokenVerificacaoRepository
    UsuarioRepositoryAdapter ..|> UsuarioRepository
    TokenVerificacaoRepositoryAdapter ..> TokenVerificacaoMapper
    TokenVerificacaoRepositoryAdapter ..> SpringDataTokenVerificacaoRepository
    UsuarioRepositoryAdapter ..> UsuarioMapper
    UsuarioRepositoryAdapter ..> SpringDataUsuarioRepository
    TokenVerificacaoMapper ..> UsuarioMapper : reusa
    TokenVerificacaoMapper ..> TokenVerificacaoJpaEntity : cria / lê
    UsuarioMapper ..> UsuarioJpaEntity : cria / lê
    SpringDataTokenVerificacaoRepository ..> TokenVerificacaoJpaEntity : persiste
    SpringDataUsuarioRepository ..> UsuarioJpaEntity : persiste
```

## Fluxo 5 — AutenticarUsuario (Login)

```mermaid
classDiagram
    direction LR

    class LoginRequest {
        <<record>>
        +email : String
        +password : String
    }
    class LoginInput {
        <<record>>
        +email : String
        +password : String
    }
    class TokenResponse {
        <<record>>
        +token : String
        +tipo : String
    }

    class AuthenticationController {
        +login(request : LoginRequest) : TokenResponse
    }

    class AutenticarUsuarioUseCase {
        +execute(input : LoginInput) : String
    }

    class AuthenticationPort {
        <<interface>>
        +autenticar(email : String, rawPassword : String) : Usuario
    }
    class TokenServicePort {
        <<interface>>
        +gerarToken(usuario : Usuario) : String
    }
    class UsuarioRepository {
        <<interface>>
        +buscarPorEmail(email : String) : Optional~Usuario~
    }

    class Usuario

    %% ---- Adapters + conversão (NOVO) ----
    class SpringAuthenticationAdapter {
        +autenticar(email : String, rawPassword : String) : Usuario
    }
    class JwtTokenAdapter {
        +gerarToken(usuario : Usuario) : String
    }
    class UsuarioRepositoryAdapter
    class SpringDataUsuarioRepository {
        <<interface>>
        +findByEmailAcad(email : String) : Optional~UsuarioJpaEntity~
    }
    class UsuarioMapper {
        +toDomain(entity : UsuarioJpaEntity) : Usuario
    }
    class UsuarioJpaEntity {
        <<Entity>>
    }

    %% ---- Relações ----
    AuthenticationController ..> LoginRequest
    AuthenticationController ..> LoginInput
    AuthenticationController ..> TokenResponse
    AuthenticationController ..> AutenticarUsuarioUseCase : - autenticarUsuarioUseCase
    AutenticarUsuarioUseCase ..> LoginInput
    AutenticarUsuarioUseCase ..> AuthenticationPort : - authenticationPort
    AutenticarUsuarioUseCase ..> TokenServicePort : - tokenServicePort
    AutenticarUsuarioUseCase ..> Usuario

    SpringAuthenticationAdapter ..|> AuthenticationPort
    JwtTokenAdapter ..|> TokenServicePort
    SpringAuthenticationAdapter ..> UsuarioRepository : - usuarioRepository
    UsuarioRepositoryAdapter ..|> UsuarioRepository
    UsuarioRepositoryAdapter ..> UsuarioMapper
    UsuarioRepositoryAdapter ..> SpringDataUsuarioRepository
    UsuarioMapper ..> UsuarioJpaEntity : lê (toDomain)
    SpringDataUsuarioRepository ..> UsuarioJpaEntity : consulta
```

> **Login não persiste:** o fluxo apenas *lê* o usuário (`buscarPorEmail` → `UsuarioMapper.toDomain`), valida senha/status e gera o JWT. Por isso só aparece o `toDomain` — não há `toEntity` nem `save`.

## Notas para o Astah

As caixas/relações marcadas com `NOVO` são as que precisam ser adicionadas; o restante já
existe nos diagramas originais.

- Adicione o **Adapter** (`ClubeRepositoryAdapter` / `PostRepositoryAdapter`) e ligue-o ao
  **port** com uma realização (linha tracejada com triângulo, `..|>` "implementa"). É o
  adapter — não o port — que orquestra a conversão.
- Adicione o **SpringDataRepository** (`SpringDataClubeRepository` / `SpringDataPostRepository`)
  com estereótipo `<<interface>>`; é a quem o adapter delega o `save`/`findById`.
- Adicione a classe **Mapper** (`ClubeMapper` / `PostMapper`) com `toEntity` e `toDomain` —
  é onde ocorre a conversão domínio → entidade.
- Adicione a **JpaEntity** (`ClubeJpaEntity` / `PostJpaEntity`) com estereótipo `<<Entity>>`.
- Adicione a entidade-filha em composição: `MembroClubeJpaEntity` / `ComentarioJpaEntity`.
- Fluxo das dependências novas: **Adapter → Mapper** e **Adapter → SpringDataRepository**;
  **Mapper → JpaEntity** ("cria/lê") e **SpringDataRepository → JpaEntity** ("persiste").
  A seta `Mapper → Clube/Post` representa o `toDomain` reconstruindo o domínio.
- O `PostJpaEntity` referencia `UsuarioJpaEntity` e `ClubeJpaEntity` via `@ManyToOne`
  (autor e clube); aqui ficam como atributos para não poluir o diagrama.
