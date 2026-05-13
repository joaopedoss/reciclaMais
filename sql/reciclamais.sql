CREATE DATABASE IF NOT EXISTS reciclamais
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE reciclamais;

CREATE TABLE IF NOT EXISTS usuarios (
    id         INT          NOT NULL AUTO_INCREMENT,
    usuario    VARCHAR(50)  NOT NULL UNIQUE,
    nome       VARCHAR(100) NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    senha      VARCHAR(255) NOT NULL,
    telefone   VARCHAR(20),
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_usuario PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS registros_reciclagem (
    id             INT            NOT NULL AUTO_INCREMENT,
    material       VARCHAR(20)    NOT NULL, 
    peso_kg        DECIMAL(8, 2)  NOT NULL,
    data_registro  DATE           NOT NULL,
    ponto_coleta   VARCHAR(100)   NOT NULL,
    observacoes    VARCHAR(255),
    usuario_id     INT            NOT NULL,
    created_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_registro    PRIMARY KEY (id),
    CONSTRAINT fk_usuario_reg FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE INDEX idx_registro_usuario   ON registros_reciclagem (usuario_id);
CREATE INDEX idx_registro_material  ON registros_reciclagem (material);
CREATE INDEX idx_registro_data      ON registros_reciclagem (data_registro);

-- ── Usuário administrador padrão 
-- senha: admin123 -  SHA-256 hash
INSERT INTO usuarios (usuario, nome, email, senha, telefone)
VALUES (
    'admin',
    'Administrador',
    'admin@reciclamais.com',
    '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', -- admin123
    '(11) 99999-9999'
);