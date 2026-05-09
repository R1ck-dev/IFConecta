-- 1. Criação da Tabela de Usuários (Simplificada para o MVP)
CREATE TABLE usuarios (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email_acad VARCHAR(255) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL, -- PENDENTE_VERIFICACAO, ATIVO, etc.
    data_criacao TIMESTAMP NOT NULL,
    prontuario VARCHAR(50) NOT NULL
);

-- 2. Criação da Tabela de Tokens de Verificação (Para ativação por email)
CREATE TABLE token_verificacao (
    id UUID PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    data_expiracao TIMESTAMP NOT NULL,
    utilizado BOOLEAN NOT NULL, 
    usuario_id UUID NOT NULL,
    CONSTRAINT fk_token_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- 3. Criação da Tabela de Clubes (Sem distinção de público/privado)
CREATE TABLE clubes (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL -- ATIVO, INATIVO
);

-- 4. Criação da Tabela de Membros do Clube (Vínculo Usuário <-> Clube)
CREATE TABLE membros_clube (
    id UUID PRIMARY KEY,
    clube_id UUID NOT NULL,
    usuario_id UUID NOT NULL,
    papel VARCHAR(50) NOT NULL, -- LIDER, MEMBRO
    data_ingresso TIMESTAMP NOT NULL, -- Corrigido para data_ingresso
    CONSTRAINT fk_membro_clube FOREIGN KEY (clube_id) REFERENCES clubes(id) ON DELETE CASCADE,
    CONSTRAINT fk_membro_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT uk_usuario_clube UNIQUE (clube_id, usuario_id)
);

-- 5. Criação da Tabela de Posts (Timeline do Clube, sem upvotes ou anonimato)
CREATE TABLE posts (
    id UUID PRIMARY KEY,
    clube_id UUID NOT NULL,
    autor_id UUID NOT NULL,
    conteudo TEXT NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    CONSTRAINT fk_post_clube FOREIGN KEY (clube_id) REFERENCES clubes(id) ON DELETE CASCADE,
    CONSTRAINT fk_post_autor FOREIGN KEY (autor_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- 6. Criação da Tabela de Comentários (Respostas aos Posts)
CREATE TABLE comentarios (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL,
    autor_id UUID NOT NULL,
    conteudo TEXT NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    CONSTRAINT fk_comentario_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    CONSTRAINT fk_comentario_autor FOREIGN KEY (autor_id) REFERENCES usuarios(id) ON DELETE CASCADE
);