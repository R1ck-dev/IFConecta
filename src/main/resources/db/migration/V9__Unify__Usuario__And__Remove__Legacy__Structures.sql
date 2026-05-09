-- Preserva dados válidos do perfil aluno durante a unificação em usuarios
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS prontuario VARCHAR(50);

UPDATE usuarios u
SET prontuario = a.prontuario
FROM alunos a
WHERE a.usuario_id = u.id
  AND (u.prontuario IS NULL OR u.prontuario = '');

ALTER TABLE usuarios
    ADD CONSTRAINT uk_usuarios_prontuario UNIQUE (prontuario);

-- Remove perfis legados não-aluno
DROP TABLE IF EXISTS institucionais;
DROP TABLE IF EXISTS professores;
DROP TABLE IF EXISTS alunos;

-- Remove estruturas legadas de upvotes
DROP TABLE IF EXISTS post_upvotes;
ALTER TABLE posts DROP COLUMN IF EXISTS qtd_upvotes;

-- Remove privacidade/solicitação de clube (modelo mínimo)
ALTER TABLE clubes DROP COLUMN IF EXISTS tipo_acesso;
ALTER TABLE membros_clube DROP COLUMN IF EXISTS status;

-- Remove notificações/comunicados legados
DROP INDEX IF EXISTS idx_notificacoes_usuario_lida;
DROP TABLE IF EXISTS notificacoes;

-- Reforça FKs relevantes após unificação de usuário
ALTER TABLE tokens_verificacao
    DROP CONSTRAINT IF EXISTS tokens_verificacao_usuario_id_fkey,
    ADD CONSTRAINT tokens_verificacao_usuario_id_fkey
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE;

ALTER TABLE posts
    DROP CONSTRAINT IF EXISTS posts_autor_id_fkey,
    ADD CONSTRAINT posts_autor_id_fkey
        FOREIGN KEY (autor_id) REFERENCES usuarios(id) ON DELETE CASCADE;

ALTER TABLE comentarios
    DROP CONSTRAINT IF EXISTS comentarios_autor_id_fkey,
    ADD CONSTRAINT comentarios_autor_id_fkey
        FOREIGN KEY (autor_id) REFERENCES usuarios(id) ON DELETE CASCADE;

ALTER TABLE membros_clube
    DROP CONSTRAINT IF EXISTS membros_clube_usuario_id_fkey,
    ADD CONSTRAINT membros_clube_usuario_id_fkey
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE;
