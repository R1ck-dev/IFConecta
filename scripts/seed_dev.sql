-- =============================================================================
-- seed_dev.sql  —  Dados de teste para DESENVOLVIMENTO/VISUALIZACAO do IFConecta
-- =============================================================================
-- NAO e uma migracao Flyway. Nunca sobe para producao nem para as branches
-- academicas. Rode manualmente contra o banco LOCAL:
--
--   docker exec -i ifconecta-db psql -U henrique_admin -d ifconecta < scripts/seed_dev.sql
--
-- ou (psql instalado):
--   psql "postgresql://henrique_admin:SENHA@localhost:5433/ifconecta" -f scripts/seed_dev.sql
--
-- Idempotente: pode rodar quantas vezes quiser. Remove o proprio seed antes de
-- reinserir (nao toca em dados reais fora dos e-mails/UUIDs dedicados abaixo).
--
-- Contas criadas (senha unica: senha123):
--   Aluno         -> dev.aluno@aluno.ifsp.edu.br
--   Professor     -> dev.professor@ifsp.edu.br
--   Institucional -> dev.institucional@ifsp.edu.br
-- Hash BCrypt ($2a$10) de "senha123", compativel com o BCryptPasswordEncoder do app.
-- =============================================================================

BEGIN;

-- -----------------------------------------------------------------------------
-- 0. Limpeza do seed anterior (idempotencia). O ON DELETE CASCADE nas FKs
--    remove posts, comentarios, upvotes, membros de clube, matriculas e
--    notificacoes ligados a estes usuarios/clubes automaticamente.
-- -----------------------------------------------------------------------------
DELETE FROM usuarios WHERE email_acad IN (
    'dev.aluno@aluno.ifsp.edu.br',
    'dev.professor@ifsp.edu.br',
    'dev.institucional@ifsp.edu.br',
    'dev.bruna@aluno.ifsp.edu.br',
    'dev.carlos@aluno.ifsp.edu.br'
);
DELETE FROM turmas      WHERE id = 'd1000000-0000-0000-0000-000000000001';
DELETE FROM disciplinas WHERE id = 'd0000000-0000-0000-0000-000000000001';
DELETE FROM clubes      WHERE id IN (
    'c0000000-0000-0000-0000-000000000001',
    'c0000000-0000-0000-0000-000000000002'
);

-- -----------------------------------------------------------------------------
-- 1. Cursos (ON CONFLICT: reaproveita se ja existirem com a mesma sigla/nome)
-- -----------------------------------------------------------------------------
INSERT INTO cursos (id, nome, sigla, modalidade) VALUES
    ('c5000000-0000-0000-0000-000000000001', 'Ciencia da Computacao',            'CComp', 'SUPERIOR'),
    ('c5000000-0000-0000-0000-000000000002', 'Analise e Desenvolvimento de Sistemas', 'ADS', 'SUPERIOR'),
    ('c5000000-0000-0000-0000-000000000003', 'Tecnico em Informatica Integrado', 'InfoInt', 'INTEGRADO')
ON CONFLICT (sigla) DO NOTHING;

-- -----------------------------------------------------------------------------
-- 2. Usuarios (tabela pai) + tabelas filhas (heranca JOINED)
--    status ATIVO = pode logar. role USER (institucional dev NAO e admin;
--    o admin real e criado pelo AdminSeeder no boot do app).
-- -----------------------------------------------------------------------------
-- 2.1 Aluno principal
INSERT INTO usuarios (id, curso_id, nome, email_acad, senha_hash, status, role, data_criacao) VALUES
    ('a0000000-0000-0000-0000-000000000001',
     (SELECT id FROM cursos WHERE sigla = 'CComp'),
     'Ana Ribeiro (Aluna)', 'dev.aluno@aluno.ifsp.edu.br',
     '$2a$10$daw.D6rI5mPYEuUUlN9MkuxWvFBL47I3zpEBIo34TEJp7H.Oi34By',
     'ATIVO', 'USER', NOW() - INTERVAL '40 days');
INSERT INTO alunos (usuario_id, prontuario) VALUES
    ('a0000000-0000-0000-0000-000000000001', 'SP3010001');

-- 2.2 Alunos extras (para dar corpo a clubes, turmas e comentarios)
INSERT INTO usuarios (id, curso_id, nome, email_acad, senha_hash, status, role, data_criacao) VALUES
    ('a0000000-0000-0000-0000-000000000002',
     (SELECT id FROM cursos WHERE sigla = 'CComp'),
     'Bruna Alves', 'dev.bruna@aluno.ifsp.edu.br',
     '$2a$10$daw.D6rI5mPYEuUUlN9MkuxWvFBL47I3zpEBIo34TEJp7H.Oi34By',
     'ATIVO', 'USER', NOW() - INTERVAL '35 days'),
    ('a0000000-0000-0000-0000-000000000003',
     (SELECT id FROM cursos WHERE sigla = 'ADS'),
     'Carlos Souza', 'dev.carlos@aluno.ifsp.edu.br',
     '$2a$10$daw.D6rI5mPYEuUUlN9MkuxWvFBL47I3zpEBIo34TEJp7H.Oi34By',
     'ATIVO', 'USER', NOW() - INTERVAL '30 days');
INSERT INTO alunos (usuario_id, prontuario) VALUES
    ('a0000000-0000-0000-0000-000000000002', 'SP3010002'),
    ('a0000000-0000-0000-0000-000000000003', 'SP3010003');

-- 2.3 Professor
INSERT INTO usuarios (id, curso_id, nome, email_acad, senha_hash, status, role, data_criacao) VALUES
    ('b0000000-0000-0000-0000-000000000001', NULL,
     'Prof. Marcos Lima', 'dev.professor@ifsp.edu.br',
     '$2a$10$daw.D6rI5mPYEuUUlN9MkuxWvFBL47I3zpEBIo34TEJp7H.Oi34By',
     'ATIVO', 'USER', NOW() - INTERVAL '60 days');
INSERT INTO professores (usuario_id, siape) VALUES
    ('b0000000-0000-0000-0000-000000000001', '1234567');

-- 2.4 Institucional (servidor)
INSERT INTO usuarios (id, curso_id, nome, email_acad, senha_hash, status, role, data_criacao) VALUES
    ('c1000000-0000-0000-0000-000000000001', NULL,
     'Coord. Julia Neves', 'dev.institucional@ifsp.edu.br',
     '$2a$10$daw.D6rI5mPYEuUUlN9MkuxWvFBL47I3zpEBIo34TEJp7H.Oi34By',
     'ATIVO', 'USER', NOW() - INTERVAL '90 days');
INSERT INTO institucionais (usuario_id, setor, cargo) VALUES
    ('c1000000-0000-0000-0000-000000000001', 'Coordenadoria de Curso', 'Coordenadora');

-- -----------------------------------------------------------------------------
-- 3. Clubes + membros
-- -----------------------------------------------------------------------------
INSERT INTO clubes (id, nome, descricao, status, tipo_acesso, data_criacao) VALUES
    ('c0000000-0000-0000-0000-000000000001',
     'Clube de Programacao Competitiva',
     'Encontros semanais para resolver problemas de maratona (ICPC/OBI) e estudar algoritmos.',
     'ATIVO', 'PUBLICO', NOW() - INTERVAL '25 days'),
    ('c0000000-0000-0000-0000-000000000002',
     'Clube de Robotica',
     'Projetos de robotica com Arduino e impressao 3D. Aberto a todos os cursos.',
     'ATIVO', 'PRIVADO', NOW() - INTERVAL '20 days');

INSERT INTO membros_clube (id, clube_id, usuario_id, papel, status, data_ingresso) VALUES
    -- Clube de Programacao: aluna Ana lidera, prof Marcos orienta, Bruna membro
    ('e0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'LIDER',      'APROVADO', NOW() - INTERVAL '25 days'),
    ('e0000000-0000-0000-0000-000000000002', 'c0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'ORIENTADOR', 'APROVADO', NOW() - INTERVAL '24 days'),
    ('e0000000-0000-0000-0000-000000000003', 'c0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000002', 'MEMBRO',     'APROVADO', NOW() - INTERVAL '20 days'),
    ('e0000000-0000-0000-0000-000000000004', 'c0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000003', 'MEMBRO',     'PENDENTE', NOW() - INTERVAL '2 days'),
    -- Clube de Robotica: Bruna lidera, Ana membro
    ('e0000000-0000-0000-0000-000000000005', 'c0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000002', 'LIDER',      'APROVADO', NOW() - INTERVAL '20 days'),
    ('e0000000-0000-0000-0000-000000000006', 'c0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000001', 'MEMBRO',     'APROVADO', NOW() - INTERVAL '18 days');

-- -----------------------------------------------------------------------------
-- 4. Posts (timeline geral: clube_id NULL) + posts de clube, comentarios, upvotes
-- -----------------------------------------------------------------------------
INSERT INTO posts (id, autor_id, clube_id, conteudo, anonimo, data_criacao) VALUES
    ('f0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', NULL,
     'Alguem sabe se a biblioteca vai abrir mais cedo na semana de provas? 📚', FALSE, NOW() - INTERVAL '3 days'),
    ('f0000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000001', NULL,
     'Lembrete: a entrega do projeto de Estruturas de Dados foi adiada para sexta. Aproveitem o fim de semana!', FALSE, NOW() - INTERVAL '2 days'),
    ('f0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000003', NULL,
     'Perdi minha garrafa azul no bloco C. Se alguem achar, avisa aqui 🙏', TRUE, NOW() - INTERVAL '1 day'),
    ('f0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000002', NULL,
     'Gente, o RU vai ter comida vegetariana todos os dias esse mes! Muito bom 🥗', FALSE, NOW() - INTERVAL '6 hours'),
    -- Post dentro do Clube de Programacao
    ('f0000000-0000-0000-0000-000000000005', 'a0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000001',
     'Resolvi montar uma lista de exercicios de grafos pra quinta. Quem topa?', FALSE, NOW() - INTERVAL '1 day');

INSERT INTO comentarios (id, post_id, autor_id, conteudo, data_criacao) VALUES
    ('c2000000-0000-0000-0000-000000000001', 'f0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000002', 'Semana passada abriu as 7h!', NOW() - INTERVAL '2 days 20 hours'),
    ('c2000000-0000-0000-0000-000000000002', 'f0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000001', 'Ufa, salvou! Obrigada professor.', NOW() - INTERVAL '1 day 22 hours'),
    ('c2000000-0000-0000-0000-000000000003', 'f0000000-0000-0000-0000-000000000005', 'a0000000-0000-0000-0000-000000000002', 'Topo! Levo alguns problemas de fluxo.', NOW() - INTERVAL '20 hours');

INSERT INTO post_upvotes (post_id, usuario_id) VALUES
    ('f0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000001'),
    ('f0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000002'),
    ('f0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000003'),
    ('f0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000001'),
    ('f0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000003'),
    ('f0000000-0000-0000-0000-000000000005', 'a0000000-0000-0000-0000-000000000002');

-- -----------------------------------------------------------------------------
-- 5. Disciplina + Turma + Matriculas
-- -----------------------------------------------------------------------------
INSERT INTO disciplinas (id, curso_id, nome, carga_horaria) VALUES
    ('d0000000-0000-0000-0000-000000000001',
     (SELECT id FROM cursos WHERE sigla = 'CComp'),
     'Estruturas de Dados', 80);

INSERT INTO turmas (id, disciplina_id, professor_id, semestre, codigo_turma, status, solicitante_id, data_criacao) VALUES
    ('d1000000-0000-0000-0000-000000000001',
     'd0000000-0000-0000-0000-000000000001',
     'b0000000-0000-0000-0000-000000000001',
     '2026.1', 'CCOMP-ED-Noturno', 'ATIVA',
     'b0000000-0000-0000-0000-000000000001', NOW() - INTERVAL '50 days');

INSERT INTO matriculas_turma (turma_id, aluno_id) VALUES
    ('d1000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001'),
    ('d1000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000002');

-- -----------------------------------------------------------------------------
-- 6. Notificacoes (para a aluna Ana ver a tela populada)
-- -----------------------------------------------------------------------------
INSERT INTO notificacoes (id, usuario_id, remetente_id, titulo, mensagem, lida, tipo_alvo, referencia_id, data_criacao) VALUES
    ('a1000000-0000-0000-0000-000000000001',
     'a0000000-0000-0000-0000-000000000001', 'c1000000-0000-0000-0000-000000000001',
     'Rematricula 2026.1 aberta',
     'A rematricula para o semestre 2026.1 esta aberta ate o dia 20. Acesse o portal e confirme suas disciplinas.',
     FALSE, 'GERAL', NULL, NOW() - INTERVAL '2 days'),
    ('a1000000-0000-0000-0000-000000000002',
     'a0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001',
     'Aviso da turma de Estruturas de Dados',
     'Pessoal, a aula de quinta sera no laboratorio 3. Levem os notebooks.',
     FALSE, 'TURMA', 'd1000000-0000-0000-0000-000000000001', NOW() - INTERVAL '1 day'),
    ('a1000000-0000-0000-0000-000000000003',
     'a0000000-0000-0000-0000-000000000001', 'c1000000-0000-0000-0000-000000000001',
     'Semana de Ciencia e Tecnologia',
     'Inscricoes abertas para a SEMCITEC! Palestras e minicursos durante toda a semana.',
     TRUE, 'CURSO', (SELECT id FROM cursos WHERE sigla = 'CComp'), NOW() - INTERVAL '10 days');

COMMIT;

-- Resumo rapido do que foi inserido
SELECT 'usuarios seed'   AS entidade, COUNT(*) FROM usuarios      WHERE email_acad LIKE 'dev.%'
UNION ALL SELECT 'clubes',            COUNT(*) FROM clubes        WHERE id::text LIKE 'c0000000-%'
UNION ALL SELECT 'posts',             COUNT(*) FROM posts         WHERE id::text LIKE 'f0000000-%'
UNION ALL SELECT 'notificacoes',      COUNT(*) FROM notificacoes  WHERE id::text LIKE 'a1000000-%';
