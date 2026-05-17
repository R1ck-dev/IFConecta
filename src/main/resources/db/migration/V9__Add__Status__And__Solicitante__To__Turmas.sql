-- Status da turma: ATIVA (padrao), PENDENTE (aguardando aprovacao do professor), REJEITADA
ALTER TABLE turmas ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'ATIVA';

-- Quem solicitou a criacao (aluno quando PENDENTE; professor quando ATIVA direto). Pode ser null para turmas antigas.
ALTER TABLE turmas ADD COLUMN solicitante_id UUID REFERENCES usuarios(id);
