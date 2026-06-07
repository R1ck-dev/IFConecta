CREATE TABLE notificacoes (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    remetente_id UUID NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    titulo VARCHAR(100) NOT NULL,
    mensagem TEXT NOT NULL,
    lida BOOLEAN NOT NULL DEFAULT FALSE,
    tipo_alvo VARCHAR(50) NOT NULL, -- GERAL, CLUBE
    referencia_id UUID, -- O ID do alvo (ex: ID do Clube)
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índice CRÍTICO de performance: O que o app mais vai fazer é buscar as notificações não lidas de um usuário
CREATE INDEX idx_notificacoes_usuario_lida ON notificacoes(usuario_id, lida);