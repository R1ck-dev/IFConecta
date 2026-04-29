-- 1. Tabela de Cursos
CREATE TABLE cursos (
    id UUID PRIMARY KEY,
    nome VARCHAR(150) NOT NULL UNIQUE,
    sigla VARCHAR(10) NOT NULL UNIQUE,
    modalidade VARCHAR(50) NOT NULL 
);

-- Adiciona a referência do Curso ao Usuário (Aluno)
ALTER TABLE usuarios ADD COLUMN curso_id UUID REFERENCES cursos(id);

-- 2. Tabela de Disciplinas
CREATE TABLE disciplinas (
    id UUID PRIMARY KEY,
    curso_id UUID NOT NULL REFERENCES cursos(id) ON DELETE CASCADE,
    nome VARCHAR(150) NOT NULL,
    carga_horaria INT NOT NULL
);

-- 3. Tabela de Turmas (A instância da disciplina em um semestre)
CREATE TABLE turmas (
    id UUID PRIMARY KEY,
    disciplina_id UUID NOT NULL REFERENCES disciplinas(id) ON DELETE CASCADE,
    professor_id UUID NOT NULL REFERENCES usuarios(id), -- O professor responsável
    semestre VARCHAR(10) NOT NULL, -- Ex: "2026.1"
    codigo_turma VARCHAR(50) NOT NULL, -- Ex: "CCOMP-Noturno"
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 4. Tabela de Matrículas (Quais alunos estão em quais turmas)
CREATE TABLE matriculas_turma (
    turma_id UUID NOT NULL REFERENCES turmas(id) ON DELETE CASCADE,
    aluno_id UUID NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    PRIMARY KEY (turma_id, aluno_id)
);