-- =====================================================================
--  IFConecta — Seed de DEMONSTRACAO (banco ifconecta_lp1)
-- =====================================================================
--  Objetivo: "dar vida" ao sistema para a apresentacao da LP1.
--
--  COMO RODAR (a partir da raiz do projeto, com o container de pe):
--    docker exec -i ifconecta-db psql -U henrique_admin -d ifconecta_lp1 \
--        -v ON_ERROR_STOP=1 < scripts/seed_demo_lp1.sql
--
--  IDEMPOTENTE: limpa TODOS os dados de demonstracao e recria do zero.
--  A conta ADMIN (criada pelo AdminSeeder) e SEMPRE preservada.
--
--  ------------------------------------------------------------------
--  CREDENCIAIS (todas as contas de demo usam a MESMA senha):
--      Senha de TODAS as contas abaixo : senha123
--
--    PROTAGONISTA (use esta para apresentar a visao de aluno):
--      ana.ribeiro@aluno.ifsp.edu.br    -> Ana Clara Ribeiro (aluna)
--          - Lider do Clube de Robotica e do Grupo de Prog. Competitiva
--          - No Grupo de Prog. Competitiva ha 2 solicitacoes PENDENTES p/ aprovar
--          - Membro do Clube de IA & Dados
--          - Solicitacao PENDENTE no Clube de Cinema (situacao "aguardando")
--          - 3 notificacoes nao lidas
--
--    OUTROS ALUNOS:
--      lucas.andrade@aluno.ifsp.edu.br      -> Lucas Andrade (lider do Clube de IA)
--      mariana.costa@aluno.ifsp.edu.br      -> Mariana Costa
--      pedro.lima@aluno.ifsp.edu.br         -> Pedro Henrique Lima
--      beatriz.almeida@aluno.ifsp.edu.br    -> Beatriz Almeida
--      gustavo.pereira@aluno.ifsp.edu.br    -> Gustavo Pereira (lider do Game Dev)
--      julia.fernandes@aluno.ifsp.edu.br    -> Julia Fernandes (lider do Cinema)
--
--    PROFESSORES (podem enviar comunicados):
--      carlos.nogueira@ifsp.edu.br          -> Prof. Carlos Eduardo Nogueira
--      renata.martins@ifsp.edu.br           -> Profa. Renata Martins
--
--    INSTITUCIONAL (envia comunicados GERAIS):
--      fernanda.oliveira@ifsp.edu.br        -> Fernanda Oliveira (Coord. de Extensao)
--
--    ADMIN (preservado; senha definida no .env do projeto):
--      henrique.marangoni@aluno.ifsp.edu.br -> senha: henrique_admin@123456
--  ------------------------------------------------------------------

BEGIN;

-- ---------------------------------------------------------------------
-- 0) LIMPEZA: remove tudo que NAO for o admin (FKs em CASCADE cuidam do resto)
-- ---------------------------------------------------------------------
DELETE FROM notificacoes;
DELETE FROM post_upvotes;
DELETE FROM comentarios;
DELETE FROM posts;
DELETE FROM membros_clube;
DELETE FROM clubes;
DELETE FROM tokens_verificacao WHERE usuario_id IN (SELECT id FROM usuarios WHERE role <> 'ADMIN');
DELETE FROM usuarios WHERE role <> 'ADMIN';

-- Hash BCrypt(strength 10) da senha "senha123" — reutilizado por todas as contas de demo.
-- (BCrypt embute o salt, entao o mesmo hash valida a mesma senha para qualquer usuario.)

-- ---------------------------------------------------------------------
-- 1) USUARIOS
-- ---------------------------------------------------------------------
INSERT INTO usuarios (id, nome, email_acad, senha_hash, status, role, data_criacao) VALUES
  -- Alunos
  ('a0000001-0000-4000-8000-000000000001', 'Ana Clara Ribeiro',     'ana.ribeiro@aluno.ifsp.edu.br',     '$2a$10$Fg3GkJvlhTqAKDSmOP0ou.Cqf1uXp6DCBu1.WkDyykZvxvAmcnM7u', 'ATIVO', 'USER', NOW() - INTERVAL '120 days'),
  ('a0000002-0000-4000-8000-000000000002', 'Lucas Andrade',         'lucas.andrade@aluno.ifsp.edu.br',   '$2a$10$Fg3GkJvlhTqAKDSmOP0ou.Cqf1uXp6DCBu1.WkDyykZvxvAmcnM7u', 'ATIVO', 'USER', NOW() - INTERVAL '118 days'),
  ('a0000003-0000-4000-8000-000000000003', 'Mariana Costa',         'mariana.costa@aluno.ifsp.edu.br',   '$2a$10$Fg3GkJvlhTqAKDSmOP0ou.Cqf1uXp6DCBu1.WkDyykZvxvAmcnM7u', 'ATIVO', 'USER', NOW() - INTERVAL '110 days'),
  ('a0000004-0000-4000-8000-000000000004', 'Pedro Henrique Lima',   'pedro.lima@aluno.ifsp.edu.br',      '$2a$10$Fg3GkJvlhTqAKDSmOP0ou.Cqf1uXp6DCBu1.WkDyykZvxvAmcnM7u', 'ATIVO', 'USER', NOW() - INTERVAL '95 days'),
  ('a0000005-0000-4000-8000-000000000005', 'Beatriz Almeida',       'beatriz.almeida@aluno.ifsp.edu.br', '$2a$10$Fg3GkJvlhTqAKDSmOP0ou.Cqf1uXp6DCBu1.WkDyykZvxvAmcnM7u', 'ATIVO', 'USER', NOW() - INTERVAL '80 days'),
  ('a0000006-0000-4000-8000-000000000006', 'Gustavo Pereira',       'gustavo.pereira@aluno.ifsp.edu.br', '$2a$10$Fg3GkJvlhTqAKDSmOP0ou.Cqf1uXp6DCBu1.WkDyykZvxvAmcnM7u', 'ATIVO', 'USER', NOW() - INTERVAL '70 days'),
  ('a0000007-0000-4000-8000-000000000007', 'Julia Fernandes',       'julia.fernandes@aluno.ifsp.edu.br', '$2a$10$Fg3GkJvlhTqAKDSmOP0ou.Cqf1uXp6DCBu1.WkDyykZvxvAmcnM7u', 'ATIVO', 'USER', NOW() - INTERVAL '150 days'),
  -- Professores
  ('b0000001-0000-4000-8000-000000000001', 'Carlos Eduardo Nogueira', 'carlos.nogueira@ifsp.edu.br',     '$2a$10$Fg3GkJvlhTqAKDSmOP0ou.Cqf1uXp6DCBu1.WkDyykZvxvAmcnM7u', 'ATIVO', 'USER', NOW() - INTERVAL '300 days'),
  ('b0000002-0000-4000-8000-000000000002', 'Renata Martins',          'renata.martins@ifsp.edu.br',      '$2a$10$Fg3GkJvlhTqAKDSmOP0ou.Cqf1uXp6DCBu1.WkDyykZvxvAmcnM7u', 'ATIVO', 'USER', NOW() - INTERVAL '280 days'),
  -- Institucional
  ('c0000001-0000-4000-8000-000000000001', 'Fernanda Oliveira',       'fernanda.oliveira@ifsp.edu.br',   '$2a$10$Fg3GkJvlhTqAKDSmOP0ou.Cqf1uXp6DCBu1.WkDyykZvxvAmcnM7u', 'ATIVO', 'USER', NOW() - INTERVAL '320 days');

INSERT INTO alunos (usuario_id, prontuario) VALUES
  ('a0000001-0000-4000-8000-000000000001', 'SL3024001'),
  ('a0000002-0000-4000-8000-000000000002', 'SL3024002'),
  ('a0000003-0000-4000-8000-000000000003', 'SL3024003'),
  ('a0000004-0000-4000-8000-000000000004', 'SL3024004'),
  ('a0000005-0000-4000-8000-000000000005', 'SL3024005'),
  ('a0000006-0000-4000-8000-000000000006', 'SL3024006'),
  ('a0000007-0000-4000-8000-000000000007', 'SL3023007');

INSERT INTO professores (usuario_id, siape) VALUES
  ('b0000001-0000-4000-8000-000000000001', '1098765'),
  ('b0000002-0000-4000-8000-000000000002', '2087654');

INSERT INTO institucionais (usuario_id, setor, cargo) VALUES
  ('c0000001-0000-4000-8000-000000000001', 'Coordenadoria de Extensao', 'Coordenadora de Extensao');

-- ---------------------------------------------------------------------
-- 2) CLUBES
-- ---------------------------------------------------------------------
INSERT INTO clubes (id, nome, descricao, status, tipo_acesso, data_criacao) VALUES
  ('d0000001-0000-4000-8000-000000000001', 'Clube de Robotica IFSP Salto',
   'Construcao de robos, automacao e participacao em competicoes como a OBR. Aberto a todos os niveis — venha aprender Arduino, sensores e impressao 3D!', 'ATIVO', 'PUBLICO', NOW() - INTERVAL '90 days'),
  ('d0000002-0000-4000-8000-000000000002', 'Grupo de Programacao Competitiva',
   'Treinos semanais de algoritmos e estruturas de dados para maratonas (ICPC, OBI). Grupo fechado: a entrada passa por aprovacao do lider.', 'ATIVO', 'PRIVADO', NOW() - INTERVAL '75 days'),
  ('d0000003-0000-4000-8000-000000000003', 'Clube de Inteligencia Artificial & Dados',
   'Estudos de Machine Learning, ciencia de dados e projetos com Python. Rodamos grupos de leitura de papers e mini-hackathons.', 'ATIVO', 'PUBLICO', NOW() - INTERVAL '60 days'),
  ('d0000004-0000-4000-8000-000000000004', 'Clube de Jogos e Game Dev',
   'Desenvolvimento de games com Godot e Unity, pixel art e game jams internas. Do conceito ao build jogavel.', 'ATIVO', 'PUBLICO', NOW() - INTERVAL '45 days'),
  ('d0000005-0000-4000-8000-000000000005', 'Clube de Cinema e Cultura',
   'Sessoes comentadas, cineclube e debates sobre cultura e tecnologia. Grupo curado com entrada por aprovacao.', 'ATIVO', 'PRIVADO', NOW() - INTERVAL '50 days');

-- ---------------------------------------------------------------------
-- 3) MEMBROS DOS CLUBES  (papel: LIDER/MEMBRO/ORIENTADOR | status: APROVADO/PENDENTE)
--    Invariante: todo clube tem exatamente 1 LIDER APROVADO.
-- ---------------------------------------------------------------------
INSERT INTO membros_clube (id, clube_id, usuario_id, papel, status, data_ingresso) VALUES
  -- Robotica (PUBLICO) — Ana e lider
  (gen_random_uuid(), 'd0000001-0000-4000-8000-000000000001', 'a0000001-0000-4000-8000-000000000001', 'LIDER',      'APROVADO', NOW() - INTERVAL '90 days'),
  (gen_random_uuid(), 'd0000001-0000-4000-8000-000000000001', 'b0000001-0000-4000-8000-000000000001', 'ORIENTADOR', 'APROVADO', NOW() - INTERVAL '89 days'),
  (gen_random_uuid(), 'd0000001-0000-4000-8000-000000000001', 'a0000002-0000-4000-8000-000000000002', 'MEMBRO',     'APROVADO', NOW() - INTERVAL '80 days'),
  (gen_random_uuid(), 'd0000001-0000-4000-8000-000000000001', 'a0000003-0000-4000-8000-000000000003', 'MEMBRO',     'APROVADO', NOW() - INTERVAL '78 days'),
  (gen_random_uuid(), 'd0000001-0000-4000-8000-000000000001', 'a0000004-0000-4000-8000-000000000004', 'MEMBRO',     'APROVADO', NOW() - INTERVAL '60 days'),

  -- Programacao Competitiva (PRIVADO) — Ana e lider; 2 solicitacoes PENDENTES p/ Ana aprovar
  (gen_random_uuid(), 'd0000002-0000-4000-8000-000000000002', 'a0000001-0000-4000-8000-000000000001', 'LIDER',      'APROVADO', NOW() - INTERVAL '75 days'),
  (gen_random_uuid(), 'd0000002-0000-4000-8000-000000000002', 'b0000002-0000-4000-8000-000000000002', 'ORIENTADOR', 'APROVADO', NOW() - INTERVAL '74 days'),
  (gen_random_uuid(), 'd0000002-0000-4000-8000-000000000002', 'a0000007-0000-4000-8000-000000000007', 'MEMBRO',     'APROVADO', NOW() - INTERVAL '70 days'),
  (gen_random_uuid(), 'd0000002-0000-4000-8000-000000000002', 'a0000006-0000-4000-8000-000000000006', 'MEMBRO',     'APROVADO', NOW() - INTERVAL '40 days'),
  (gen_random_uuid(), 'd0000002-0000-4000-8000-000000000002', 'a0000005-0000-4000-8000-000000000005', 'MEMBRO',     'PENDENTE', NOW() - INTERVAL '3 days'),
  (gen_random_uuid(), 'd0000002-0000-4000-8000-000000000002', 'a0000004-0000-4000-8000-000000000004', 'MEMBRO',     'PENDENTE', NOW() - INTERVAL '1 days'),

  -- IA & Dados (PUBLICO) — Lucas e lider; Ana e membro
  (gen_random_uuid(), 'd0000003-0000-4000-8000-000000000003', 'a0000002-0000-4000-8000-000000000002', 'LIDER',      'APROVADO', NOW() - INTERVAL '60 days'),
  (gen_random_uuid(), 'd0000003-0000-4000-8000-000000000003', 'b0000002-0000-4000-8000-000000000002', 'ORIENTADOR', 'APROVADO', NOW() - INTERVAL '58 days'),
  (gen_random_uuid(), 'd0000003-0000-4000-8000-000000000003', 'a0000001-0000-4000-8000-000000000001', 'MEMBRO',     'APROVADO', NOW() - INTERVAL '55 days'),
  (gen_random_uuid(), 'd0000003-0000-4000-8000-000000000003', 'a0000003-0000-4000-8000-000000000003', 'MEMBRO',     'APROVADO', NOW() - INTERVAL '50 days'),
  (gen_random_uuid(), 'd0000003-0000-4000-8000-000000000003', 'a0000005-0000-4000-8000-000000000005', 'MEMBRO',     'APROVADO', NOW() - INTERVAL '30 days'),

  -- Game Dev (PUBLICO) — Gustavo e lider; Ana NAO participa
  (gen_random_uuid(), 'd0000004-0000-4000-8000-000000000004', 'a0000006-0000-4000-8000-000000000006', 'LIDER',      'APROVADO', NOW() - INTERVAL '45 days'),
  (gen_random_uuid(), 'd0000004-0000-4000-8000-000000000004', 'a0000004-0000-4000-8000-000000000004', 'MEMBRO',     'APROVADO', NOW() - INTERVAL '40 days'),
  (gen_random_uuid(), 'd0000004-0000-4000-8000-000000000004', 'a0000005-0000-4000-8000-000000000005', 'MEMBRO',     'APROVADO', NOW() - INTERVAL '35 days'),
  (gen_random_uuid(), 'd0000004-0000-4000-8000-000000000004', 'a0000007-0000-4000-8000-000000000007', 'MEMBRO',     'APROVADO', NOW() - INTERVAL '20 days'),

  -- Cinema (PRIVADO) — Julia e lider; Ana com solicitacao PENDENTE (visao "aguardando")
  (gen_random_uuid(), 'd0000005-0000-4000-8000-000000000005', 'a0000007-0000-4000-8000-000000000007', 'LIDER',      'APROVADO', NOW() - INTERVAL '50 days'),
  (gen_random_uuid(), 'd0000005-0000-4000-8000-000000000005', 'b0000001-0000-4000-8000-000000000001', 'ORIENTADOR', 'APROVADO', NOW() - INTERVAL '48 days'),
  (gen_random_uuid(), 'd0000005-0000-4000-8000-000000000005', 'a0000003-0000-4000-8000-000000000003', 'MEMBRO',     'APROVADO', NOW() - INTERVAL '40 days'),
  (gen_random_uuid(), 'd0000005-0000-4000-8000-000000000005', 'a0000005-0000-4000-8000-000000000005', 'MEMBRO',     'APROVADO', NOW() - INTERVAL '25 days'),
  (gen_random_uuid(), 'd0000005-0000-4000-8000-000000000005', 'a0000001-0000-4000-8000-000000000001', 'MEMBRO',     'PENDENTE', NOW() - INTERVAL '2 days');

-- ---------------------------------------------------------------------
-- 4) POSTS
--    Timeline GERAL  = clube_id NULL
--    Timeline CLUBE  = clube_id preenchido (so aparece dentro do clube)
-- ---------------------------------------------------------------------
INSERT INTO posts (id, autor_id, clube_id, conteudo, anonimo, data_criacao) VALUES
  -- ===== TIMELINE GERAL (clube_id NULL) =====
  ('e0000001-0000-4000-8000-000000000001', 'a0000002-0000-4000-8000-000000000002', NULL,
   'Bom dia, IFSP Salto! Alguem mais animado com o inicio do semestre? Bora trocar figurinha sobre os projetos de cada um por aqui. 🚀', FALSE, NOW() - INTERVAL '9 days'),
  ('e0000002-0000-4000-8000-000000000002', 'a0000003-0000-4000-8000-000000000003', NULL,
   'Gente, alguem tem material bom de Calculo II? To penando com integrais por partes. Aceito indicacoes de videoaula tambem 🙏', FALSE, NOW() - INTERVAL '8 days'),
  ('e0000003-0000-4000-8000-000000000003', 'a0000001-0000-4000-8000-000000000001', NULL,
   'O Clube de Robotica vai ter inscricoes abertas essa semana! Vamos comecar um projeto pra OBR. Se interessou, comenta aqui ou entra no clube. 🤖', FALSE, NOW() - INTERVAL '7 days'),
  ('e0000004-0000-4000-8000-000000000004', 'a0000004-0000-4000-8000-000000000004', NULL,
   'Desabafo: estudei a semana toda e a prova cobrou exatamente o unico topico que eu pulei. A vida de estudante e isso mesmo kkkk', TRUE, NOW() - INTERVAL '6 days'),
  ('e0000005-0000-4000-8000-000000000005', 'a0000005-0000-4000-8000-000000000005', NULL,
   'Saiu uma vaga de estagio de desenvolvimento aqui em Salto, presencial 4h/dia. Quem quiser o link manda DM que eu compartilho! 💼', FALSE, NOW() - INTERVAL '5 days'),
  ('e0000006-0000-4000-8000-000000000006', 'a0000006-0000-4000-8000-000000000006', NULL,
   'Quem ai curte game dev? To organizando uma game jam interna do campus pro mes que vem. Bora montar uns times! 🎮', FALSE, NOW() - INTERVAL '4 days'),
  ('e0000007-0000-4000-8000-000000000007', 'a0000007-0000-4000-8000-000000000007', NULL,
   'Dica de leitura pra quem ta comecando em programacao: "Codigo Limpo" do Uncle Bob. Mudou minha forma de escrever codigo. 📚', FALSE, NOW() - INTERVAL '3 days'),
  ('e0000008-0000-4000-8000-000000000008', 'b0000002-0000-4000-8000-000000000002', NULL,
   'Pessoal, a monitoria de Estrutura de Dados volta na segunda, das 14h as 16h, no laboratorio 3. Tragam suas duvidas! — Profa. Renata', FALSE, NOW() - INTERVAL '2 days'),

  -- ===== CLUBE: Robotica =====
  ('e0000009-0000-4000-8000-000000000009', 'a0000001-0000-4000-8000-000000000001', 'd0000001-0000-4000-8000-000000000001',
   'Reuniao de kickoff confirmada pra sexta, 17h, no lab de eletronica. Vamos definir os subgrupos: mecanica, eletronica e programacao. Quem prefere o que?', FALSE, NOW() - INTERVAL '6 days'),
  ('e0000010-0000-4000-8000-000000000010', 'b0000001-0000-4000-8000-000000000001', 'd0000001-0000-4000-8000-000000000001',
   'Deixei na pasta compartilhada o roteiro da primeira oficina de Arduino. Leiam antes da sexta pra render mais. — Prof. Carlos', FALSE, NOW() - INTERVAL '5 days'),
  ('e0000011-0000-4000-8000-000000000011', 'a0000002-0000-4000-8000-000000000002', 'd0000001-0000-4000-8000-000000000001',
   'As pecas que pedimos chegaram! Servos, sensores ultrassonicos e a ponte H. Ja ta tudo na bancada do lab. 🔧', FALSE, NOW() - INTERVAL '2 days'),

  -- ===== CLUBE: Programacao Competitiva =====
  ('e0000012-0000-4000-8000-000000000012', 'a0000001-0000-4000-8000-000000000001', 'd0000002-0000-4000-8000-000000000002',
   'Subi a lista 1 de treino: foco em complexidade e busca binaria. Tentem resolver ate quarta que ai discutimos as abordagens juntos.', FALSE, NOW() - INTERVAL '5 days'),
  ('e0000013-0000-4000-8000-000000000013', 'a0000007-0000-4000-8000-000000000007', 'd0000002-0000-4000-8000-000000000002',
   'Travei no problema D (grafos). Alguem conseguiu? Acho que da pra resolver com BFS, mas to estourando o tempo limite.', FALSE, NOW() - INTERVAL '3 days'),

  -- ===== CLUBE: IA & Dados =====
  ('e0000014-0000-4000-8000-000000000014', 'a0000002-0000-4000-8000-000000000002', 'd0000003-0000-4000-8000-000000000003',
   'Escolhi o dataset do nosso primeiro projeto: previsao de evasao escolar com dados publicos. Topam? E uma causa que combina com o IFSP.', FALSE, NOW() - INTERVAL '7 days'),
  ('e0000015-0000-4000-8000-000000000015', 'a0000001-0000-4000-8000-000000000001', 'd0000003-0000-4000-8000-000000000003',
   'Duvida de pandas: qual a melhor forma de tratar valores faltantes nesse dataset? To em duvida entre dropar as linhas ou preencher com a mediana.', FALSE, NOW() - INTERVAL '4 days'),
  ('e0000016-0000-4000-8000-000000000016', 'b0000002-0000-4000-8000-000000000002', 'd0000003-0000-4000-8000-000000000003',
   'Separei um paper introdutorio sobre arvores de decisao pro nosso grupo de leitura. E bem didatico, otimo ponto de partida. — Profa. Renata', FALSE, NOW() - INTERVAL '3 days'),

  -- ===== CLUBE: Game Dev =====
  ('e0000017-0000-4000-8000-000000000017', 'a0000006-0000-4000-8000-000000000006', 'd0000004-0000-4000-8000-000000000004',
   'Tema da nossa primeira game jam: "gravidade invertida". 48h pra fazer um prototipo jogavel. Quem ta dentro? 🎮', FALSE, NOW() - INTERVAL '4 days'),
  ('e0000018-0000-4000-8000-000000000018', 'a0000004-0000-4000-8000-000000000004', 'd0000004-0000-4000-8000-000000000004',
   'Fiz uns sprites de teste no Aseprite pro nosso personagem. Subi as imagens na pasta do clube, deem uma olhada e mandem feedback!', FALSE, NOW() - INTERVAL '2 days'),

  -- ===== CLUBE: Cinema =====
  ('e0000019-0000-4000-8000-000000000019', 'a0000007-0000-4000-8000-000000000007', 'd0000005-0000-4000-8000-000000000005',
   'Sessao dessa semana: "O Homem Que Mudou o Jogo". Depois rola um debate sobre dados no esporte. Sexta, 18h30, no auditorio.', FALSE, NOW() - INTERVAL '4 days'),
  ('e0000020-0000-4000-8000-000000000020', 'a0000003-0000-4000-8000-000000000003', 'd0000005-0000-4000-8000-000000000005',
   'Enquete pro proximo encontro: ficcao cientifica ou documentario de tecnologia? Comentem a preferencia de voces!', FALSE, NOW() - INTERVAL '1 days');

-- ---------------------------------------------------------------------
-- 5) UPVOTES  (PK composta post_id+usuario_id — sem duplicatas)
--    Ana (protagonista) curtiu alguns posts -> "jaDeiUpvote" = true neles.
-- ---------------------------------------------------------------------
INSERT INTO post_upvotes (post_id, usuario_id) VALUES
  -- p01 (Lucas: bom dia) — bem popular
  ('e0000001-0000-4000-8000-000000000001', 'a0000001-0000-4000-8000-000000000001'),
  ('e0000001-0000-4000-8000-000000000001', 'a0000003-0000-4000-8000-000000000003'),
  ('e0000001-0000-4000-8000-000000000001', 'a0000004-0000-4000-8000-000000000004'),
  ('e0000001-0000-4000-8000-000000000001', 'a0000005-0000-4000-8000-000000000005'),
  ('e0000001-0000-4000-8000-000000000001', 'a0000007-0000-4000-8000-000000000007'),
  -- p02 (Mariana: calculo)
  ('e0000002-0000-4000-8000-000000000002', 'a0000004-0000-4000-8000-000000000004'),
  ('e0000002-0000-4000-8000-000000000002', 'a0000005-0000-4000-8000-000000000005'),
  -- p03 (Ana: robotica)
  ('e0000003-0000-4000-8000-000000000003', 'a0000002-0000-4000-8000-000000000002'),
  ('e0000003-0000-4000-8000-000000000003', 'a0000003-0000-4000-8000-000000000003'),
  ('e0000003-0000-4000-8000-000000000003', 'a0000004-0000-4000-8000-000000000004'),
  ('e0000003-0000-4000-8000-000000000003', 'b0000001-0000-4000-8000-000000000001'),
  -- p04 (anonimo: desabafo) — muito popular (todo mundo se identifica)
  ('e0000004-0000-4000-8000-000000000004', 'a0000001-0000-4000-8000-000000000001'),
  ('e0000004-0000-4000-8000-000000000004', 'a0000002-0000-4000-8000-000000000002'),
  ('e0000004-0000-4000-8000-000000000004', 'a0000003-0000-4000-8000-000000000003'),
  ('e0000004-0000-4000-8000-000000000004', 'a0000005-0000-4000-8000-000000000005'),
  ('e0000004-0000-4000-8000-000000000004', 'a0000006-0000-4000-8000-000000000006'),
  ('e0000004-0000-4000-8000-000000000004', 'a0000007-0000-4000-8000-000000000007'),
  -- p05 (Beatriz: vaga estagio)
  ('e0000005-0000-4000-8000-000000000005', 'a0000001-0000-4000-8000-000000000001'),
  ('e0000005-0000-4000-8000-000000000005', 'a0000002-0000-4000-8000-000000000002'),
  ('e0000005-0000-4000-8000-000000000005', 'a0000004-0000-4000-8000-000000000004'),
  -- p06 (Gustavo: game jam)
  ('e0000006-0000-4000-8000-000000000006', 'a0000004-0000-4000-8000-000000000004'),
  ('e0000006-0000-4000-8000-000000000006', 'a0000005-0000-4000-8000-000000000005'),
  -- p07 (Julia: livro) — Ana curtiu
  ('e0000007-0000-4000-8000-000000000007', 'a0000001-0000-4000-8000-000000000001'),
  ('e0000007-0000-4000-8000-000000000007', 'a0000002-0000-4000-8000-000000000002'),
  ('e0000007-0000-4000-8000-000000000007', 'a0000003-0000-4000-8000-000000000003'),
  -- p08 (Profa Renata: monitoria)
  ('e0000008-0000-4000-8000-000000000008', 'a0000001-0000-4000-8000-000000000001'),
  ('e0000008-0000-4000-8000-000000000008', 'a0000003-0000-4000-8000-000000000003'),
  ('e0000008-0000-4000-8000-000000000008', 'a0000004-0000-4000-8000-000000000004'),
  -- posts de clube com algumas curtidas
  ('e0000011-0000-4000-8000-000000000011', 'a0000001-0000-4000-8000-000000000001'),
  ('e0000011-0000-4000-8000-000000000011', 'a0000003-0000-4000-8000-000000000003'),
  ('e0000014-0000-4000-8000-000000000014', 'a0000001-0000-4000-8000-000000000001'),
  ('e0000014-0000-4000-8000-000000000014', 'a0000003-0000-4000-8000-000000000003'),
  ('e0000014-0000-4000-8000-000000000014', 'a0000005-0000-4000-8000-000000000005'),
  ('e0000017-0000-4000-8000-000000000017', 'a0000001-0000-4000-8000-000000000001'),
  ('e0000017-0000-4000-8000-000000000017', 'a0000004-0000-4000-8000-000000000004'),
  ('e0000017-0000-4000-8000-000000000017', 'a0000005-0000-4000-8000-000000000005'),
  ('e0000019-0000-4000-8000-000000000019', 'a0000003-0000-4000-8000-000000000003'),
  ('e0000019-0000-4000-8000-000000000019', 'a0000005-0000-4000-8000-000000000005');

-- ---------------------------------------------------------------------
-- 6) COMENTARIOS
-- ---------------------------------------------------------------------
INSERT INTO comentarios (id, post_id, autor_id, conteudo, data_criacao) VALUES
  -- p01 (bom dia)
  (gen_random_uuid(), 'e0000001-0000-4000-8000-000000000001', 'a0000001-0000-4000-8000-000000000001', 'Bora! To no Clube de Robotica esse semestre, super recomendo. 🤖', NOW() - INTERVAL '9 days' + INTERVAL '2 hours'),
  (gen_random_uuid(), 'e0000001-0000-4000-8000-000000000001', 'a0000007-0000-4000-8000-000000000007', 'Animadissima! Esse ano vou tentar a maratona de programacao.', NOW() - INTERVAL '8 days'),
  -- p02 (calculo)
  (gen_random_uuid(), 'e0000002-0000-4000-8000-000000000002', 'a0000004-0000-4000-8000-000000000004', 'Da uma olhada nos videos do "Equaciona com Paulo Pereira", salvou minha vida em Calculo I.', NOW() - INTERVAL '8 days' + INTERVAL '3 hours'),
  (gen_random_uuid(), 'e0000002-0000-4000-8000-000000000002', 'a0000001-0000-4000-8000-000000000001', 'Tenho uma lista de exercicios resolvidos, te mando!', NOW() - INTERVAL '7 days'),
  -- p03 (robotica)
  (gen_random_uuid(), 'e0000003-0000-4000-8000-000000000003', 'a0000004-0000-4000-8000-000000000004', 'Quero entrar! Nunca mexi com Arduino mas to afim de aprender.', NOW() - INTERVAL '6 days'),
  (gen_random_uuid(), 'e0000003-0000-4000-8000-000000000003', 'b0000001-0000-4000-8000-000000000001', 'Excelente iniciativa, Ana. Conta comigo na orientacao. — Prof. Carlos', NOW() - INTERVAL '6 days' + INTERVAL '5 hours'),
  -- p04 (anonimo)
  (gen_random_uuid(), 'e0000004-0000-4000-8000-000000000004', 'a0000003-0000-4000-8000-000000000003', 'kkkkk forca, e a famosa lei de Murphy academica', NOW() - INTERVAL '6 days' + INTERVAL '1 hours'),
  (gen_random_uuid(), 'e0000004-0000-4000-8000-000000000004', 'a0000002-0000-4000-8000-000000000002', 'Sentimento universal isso aqui hahaha', NOW() - INTERVAL '5 days'),
  -- p05 (vaga)
  (gen_random_uuid(), 'e0000005-0000-4000-8000-000000000005', 'a0000002-0000-4000-8000-000000000002', 'Interessado! Ja te mandei DM, valeu por compartilhar.', NOW() - INTERVAL '5 days' + INTERVAL '2 hours'),
  -- p06 (game jam)
  (gen_random_uuid(), 'e0000006-0000-4000-8000-000000000006', 'a0000004-0000-4000-8000-000000000004', 'Eu topo! Posso cuidar da arte.', NOW() - INTERVAL '4 days' + INTERVAL '1 hours'),
  (gen_random_uuid(), 'e0000006-0000-4000-8000-000000000006', 'a0000005-0000-4000-8000-000000000005', 'Conta comigo na programacao. 🎮', NOW() - INTERVAL '3 days'),
  -- p08 (monitoria)
  (gen_random_uuid(), 'e0000008-0000-4000-8000-000000000008', 'a0000003-0000-4000-8000-000000000003', 'Obrigada, professora! Vou levar minhas duvidas de listas encadeadas.', NOW() - INTERVAL '2 days' + INTERVAL '4 hours'),
  -- p09 (robotica kickoff)
  (gen_random_uuid(), 'e0000009-0000-4000-8000-000000000009', 'a0000002-0000-4000-8000-000000000002', 'Eu fico na eletronica! Ja tenho uma experiencia com solda.', NOW() - INTERVAL '6 days' + INTERVAL '3 hours'),
  (gen_random_uuid(), 'e0000009-0000-4000-8000-000000000009', 'a0000004-0000-4000-8000-000000000004', 'Quero a programacao, to estudando C pra Arduino.', NOW() - INTERVAL '5 days'),
  -- p13 (progcomp grafos)
  (gen_random_uuid(), 'e0000013-0000-4000-8000-000000000013', 'a0000001-0000-4000-8000-000000000001', 'Tenta trocar a estrutura da fila por um deque, da uma boa otimizada no BFS.', NOW() - INTERVAL '2 days'),
  -- p14 (ia dataset)
  (gen_random_uuid(), 'e0000014-0000-4000-8000-000000000014', 'a0000001-0000-4000-8000-000000000001', 'Topo demais! Tema super relevante. Posso ajudar na parte de visualizacao dos dados.', NOW() - INTERVAL '7 days' + INTERVAL '2 hours'),
  (gen_random_uuid(), 'e0000014-0000-4000-8000-000000000014', 'a0000005-0000-4000-8000-000000000005', 'Adorei a ideia, conta comigo!', NOW() - INTERVAL '6 days'),
  -- p15 (pandas)
  (gen_random_uuid(), 'e0000015-0000-4000-8000-000000000015', 'a0000002-0000-4000-8000-000000000002', 'Depende da proporcao de faltantes. Se for pouco, mediana resolve bem sem perder dados.', NOW() - INTERVAL '4 days' + INTERVAL '2 hours'),
  -- p17 (game jam tema)
  (gen_random_uuid(), 'e0000017-0000-4000-8000-000000000017', 'a0000005-0000-4000-8000-000000000005', 'Gravidade invertida e um prato cheio pra plataforma. Bora!', NOW() - INTERVAL '3 days'),
  -- p19 (cinema)
  (gen_random_uuid(), 'e0000019-0000-4000-8000-000000000019', 'a0000003-0000-4000-8000-000000000003', 'Esse filme e otimo pra puxar o debate sobre dados. Vou levar uns amigos!', NOW() - INTERVAL '3 days');

-- ---------------------------------------------------------------------
-- 7) NOTIFICACOES / COMUNICADOS
--    usuario_id = destinatario | remetente_id = quem enviou
--    GERAL -> todos os usuarios ATIVOS (exceto o remetente)
--    CLUBE -> membros APROVADOS do clube (exceto o remetente)
--    referencia_id = id do clube (apenas em comunicados de CLUBE)
-- ---------------------------------------------------------------------

-- (GERAL #1) Fernanda (institucional) -> Semana de Tecnologia  [NAO LIDA]
INSERT INTO notificacoes (id, usuario_id, remetente_id, titulo, mensagem, lida, tipo_alvo, referencia_id, data_criacao)
SELECT gen_random_uuid(), u.id, 'c0000001-0000-4000-8000-000000000001',
       'Semana de Tecnologia 2026 — inscricoes abertas',
       'Estao abertas as inscricoes para a Semana de Tecnologia do campus, de 14 a 18 de julho. Palestras, oficinas e mostra de projetos. Inscreva-se pela coordenadoria de extensao!',
       FALSE, 'GERAL', NULL, NOW() - INTERVAL '2 days'
FROM usuarios u
WHERE u.status = 'ATIVO' AND u.id <> 'c0000001-0000-4000-8000-000000000001';

-- (GERAL #2) Fernanda (institucional) -> Horario da Biblioteca  [LIDA]
INSERT INTO notificacoes (id, usuario_id, remetente_id, titulo, mensagem, lida, tipo_alvo, referencia_id, data_criacao)
SELECT gen_random_uuid(), u.id, 'c0000001-0000-4000-8000-000000000001',
       'Biblioteca: novo horario de funcionamento',
       'A partir desta semana a biblioteca do campus passa a funcionar das 8h as 21h em dias letivos. Bons estudos!',
       TRUE, 'GERAL', NULL, NOW() - INTERVAL '12 days'
FROM usuarios u
WHERE u.status = 'ATIVO' AND u.id <> 'c0000001-0000-4000-8000-000000000001';

-- (GERAL #3) ADMIN -> Boas-vindas ao IFConecta  [LIDA]
INSERT INTO notificacoes (id, usuario_id, remetente_id, titulo, mensagem, lida, tipo_alvo, referencia_id, data_criacao)
SELECT gen_random_uuid(), u.id, adm.id,
       'Bem-vindo(a) ao IFConecta!',
       'Esta e a rede social academica do IFSP Salto. Explore os clubes, participe das discussoes na timeline e fique por dentro dos comunicados. Boa jornada!',
       TRUE, 'GERAL', NULL, NOW() - INTERVAL '20 days'
FROM usuarios u
CROSS JOIN (SELECT id FROM usuarios WHERE role = 'ADMIN' ORDER BY data_criacao LIMIT 1) adm
WHERE u.status = 'ATIVO' AND u.id <> adm.id;

-- (CLUBE: IA & Dados) Lucas (lider) -> Reuniao do clube  [NAO LIDA]
INSERT INTO notificacoes (id, usuario_id, remetente_id, titulo, mensagem, lida, tipo_alvo, referencia_id, data_criacao)
SELECT gen_random_uuid(), m.usuario_id, 'a0000002-0000-4000-8000-000000000002',
       'Reuniao do Clube de IA — quinta, 18h',
       'Pessoal, nossa proxima reuniao sera quinta as 18h no laboratorio 2. Pauta: definir as duplas do projeto de evasao escolar. Tragam o notebook!',
       FALSE, 'CLUBE', 'd0000003-0000-4000-8000-000000000003', NOW() - INTERVAL '1 days'
FROM membros_clube m
WHERE m.clube_id = 'd0000003-0000-4000-8000-000000000003'
  AND m.status = 'APROVADO'
  AND m.usuario_id <> 'a0000002-0000-4000-8000-000000000002';

-- (CLUBE: Robotica) Prof. Carlos (orientador) -> Oficina  [NAO LIDA]
INSERT INTO notificacoes (id, usuario_id, remetente_id, titulo, mensagem, lida, tipo_alvo, referencia_id, data_criacao)
SELECT gen_random_uuid(), m.usuario_id, 'b0000001-0000-4000-8000-000000000001',
       'Oficina de Arduino — traga seu notebook',
       'Lembrete da nossa oficina de sexta, 17h, no lab de eletronica. Quem tiver notebook, traga com a IDE do Arduino ja instalada para agilizar.',
       FALSE, 'CLUBE', 'd0000001-0000-4000-8000-000000000001', NOW() - INTERVAL '3 days'
FROM membros_clube m
WHERE m.clube_id = 'd0000001-0000-4000-8000-000000000001'
  AND m.status = 'APROVADO'
  AND m.usuario_id <> 'b0000001-0000-4000-8000-000000000001';

-- (GERAL #4) Fernanda (institucional) -> Edital de monitoria  [LIDA]
INSERT INTO notificacoes (id, usuario_id, remetente_id, titulo, mensagem, lida, tipo_alvo, referencia_id, data_criacao)
SELECT gen_random_uuid(), u.id, 'c0000001-0000-4000-8000-000000000001',
       'Resultado do edital de monitoria',
       'O resultado do edital de monitoria 2026/1 ja esta disponivel no mural da coordenadoria. Parabens aos selecionados!',
       TRUE, 'GERAL', NULL, NOW() - INTERVAL '15 days'
FROM usuarios u
WHERE u.status = 'ATIVO' AND u.id <> 'c0000001-0000-4000-8000-000000000001';

COMMIT;

-- ---------------------------------------------------------------------
-- 8) RESUMO (executa fora da transacao, apenas para conferencia visual)
-- ---------------------------------------------------------------------
\echo ''
\echo '================ RESUMO DO SEED ================'
SELECT 'usuarios'      AS tabela, count(*) FROM usuarios
UNION ALL SELECT 'alunos',        count(*) FROM alunos
UNION ALL SELECT 'professores',   count(*) FROM professores
UNION ALL SELECT 'institucionais',count(*) FROM institucionais
UNION ALL SELECT 'clubes',        count(*) FROM clubes
UNION ALL SELECT 'membros_clube', count(*) FROM membros_clube
UNION ALL SELECT 'posts',         count(*) FROM posts
UNION ALL SELECT 'comentarios',   count(*) FROM comentarios
UNION ALL SELECT 'post_upvotes',  count(*) FROM post_upvotes
UNION ALL SELECT 'notificacoes',  count(*) FROM notificacoes
ORDER BY tabela;
