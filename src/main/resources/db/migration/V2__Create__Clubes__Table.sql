-- V2__Create_Clubes_Table.sql

CREATE TABLE clubes (
    id UUID PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,      -- ATIVO, INATIVO
    tipo_acesso VARCHAR(50) NOT NULL, -- PUBLICO, PRIVADO
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE membros_clube (
    id UUID PRIMARY KEY,
    clube_id UUID NOT NULL REFERENCES clubes(id) ON DELETE CASCADE,
    usuario_id UUID NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    papel VARCHAR(50) NOT NULL,       -- LIDER, MEMBRO, ORIENTADOR
    status VARCHAR(50) NOT NULL,      -- PENDENTE, APROVADO
    data_ingresso TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(clube_id, usuario_id)      -- Um usuário não pode entrar no mesmo clube duas vezes
);