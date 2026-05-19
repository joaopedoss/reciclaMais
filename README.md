# ♻️ ReciclaMAIS

Sistema de registro e acompanhamento de reciclagem, desenvolvido em Java com banco de dados MySQL.

---

## Sobre o Projeto

O **ReciclaMAIS** permite que usuários registrem suas atividades de reciclagem, informando o tipo de material, peso, ponto de coleta e data. O objetivo é incentivar e monitorar hábitos sustentáveis de forma simples e organizada.

---

## Banco de Dados

O projeto utiliza **MySQL** com charset `utf8mb4`.

### Tabelas

#### `usuarios`
| Coluna       | Tipo           | Descrição                        |
|--------------|----------------|----------------------------------|
| `id`         | INT (PK)       | Identificador único              |
| `usuario`    | VARCHAR(50)    | Nome de usuário (único)          |
| `nome`       | VARCHAR(100)   | Nome completo                    |
| `email`      | VARCHAR(100)   | E-mail (único)                   |
| `senha`      | VARCHAR(255)   | Hash SHA-256 da senha            |
| `telefone`   | VARCHAR(20)    | Telefone (opcional)              |
| `created_at` | TIMESTAMP      | Data de criação do registro      |

#### `registros_reciclagem`
| Coluna          | Tipo           | Descrição                          |
|-----------------|----------------|------------------------------------|
| `id`            | INT (PK)       | Identificador único                |
| `material`      | VARCHAR(20)    | Tipo de material reciclado         |
| `peso_kg`       | DECIMAL(8,2)   | Peso em quilogramas                |
| `data_registro` | DATE           | Data da reciclagem                 |
| `ponto_coleta`  | VARCHAR(100)   | Local de entrega do material       |
| `observacoes`   | VARCHAR(255)   | Observações adicionais (opcional)  |
| `usuario_id`    | INT (FK)       | Referência ao usuário              |
| `created_at`    | TIMESTAMP      | Data de criação do registro        |

---

## Como Configurar

### Pré-requisitos

- Java (JDK 11 ou superior)
- MySQL 8.0+
- IntelliJ IDEA (IDE utilizada) ou outra IDE Java

### 1. Clonar o repositório

```
git clone https://github.com/seu-usuario/reciclamais.git
cd reciclamais
```

### 2. Configurar o banco de dados

Acesse o MySQL e execute o script de criação:

```
mysql -u root -p < reciclamais.sql
```

Isso irá:
- Criar o banco de dados `reciclamais`
- Criar as tabelas `usuarios` e `registros_reciclagem`
- Inserir o usuário administrador padrão

### 3. Usuário administrador padrão

| Campo    | Valor                    |
|----------|--------------------------|
| Usuário  | `admin`                  |
| E-mail   | `admin@reciclamais.com`  |
| Senha    | `admin123`               |

> **Altere a senha do administrador após o primeiro acesso.**

### 4. Configurar a conexão com o banco

Atualize as credenciais de conexão no arquivo de configuração do projeto com os dados do seu ambiente MySQL local.

---

## Estrutura do Projeto

```
reciclamais/
├── src/                    # Código-fonte Java
├── reciclamais.sql         # Script de criação do banco de dados
├── README.md
├── pom.xml
└── .gitignore

```

---

## Segurança

- As senhas são armazenadas como hash **SHA-256**.
- A exclusão de um usuário remove automaticamente todos os seus registros de reciclagem (CASCADE).


