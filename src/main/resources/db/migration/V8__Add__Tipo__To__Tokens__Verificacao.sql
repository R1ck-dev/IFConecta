-- V8__Add__Tipo__To__Tokens__Verificacao.sql
-- Distingue tokens de ATIVACAO de conta vs REDEFINICAO_SENHA.

ALTER TABLE tokens_verificacao
    ADD COLUMN tipo VARCHAR(50) NOT NULL DEFAULT 'ATIVACAO';
