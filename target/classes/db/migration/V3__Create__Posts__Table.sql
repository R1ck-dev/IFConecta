-- V3__Create_Posts_Table.sql

CREATE TABLE posts (
    id UUID PRIMARY KEY,
    autor_id UUID NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    clube_id UUID REFERENCES clubes(id) ON DELETE CASCADE, -- NULL significa que é da Timeline Geral
    conteudo TEXT NOT NULL,
    qtd_upvotes INT NOT NULL DEFAULT 0,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE comentarios (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    autor_id UUID NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    conteudo TEXT NOT NULL,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);