-- Crie um novo arquivo: src/main/resources/db/migration/V2__add_senha_to_usuario.sql
ALTER TABLE usuario ADD COLUMN senha VARCHAR(255) NOT NULL DEFAULT 'temp_password'; -- Adicione um DEFAULT temporário
ALTER TABLE usuario ALTER COLUMN senha DROP DEFAULT; -- Remova o default após a migração