-- Cria a tabela de relacionamento 
CREATE TABLE post_upvotes (
    post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    usuario_id UUID NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    PRIMARY KEY (post_id, usuario_id)
);

-- Remove a coluna antiga, pois agora calculamos os upvotes dinamicamente
ALTER TABLE posts DROP COLUMN qtd_upvotes;