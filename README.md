# FinTrackAPI

API REST para gerenciamento de finanças pessoais, desenvolvida com Java e Spring Boot.

Projeto desenvolvido como projeto final do curso de programação Java, aplicando conceitos de desenvolvimento backend, APIs REST, persistência de dados, autenticação e segurança.

## Tecnologias

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Spring Security
- JWT
- Bean Validation
- MySQL
- H2 Database
- Swagger / OpenAPI
- Maven

## Funcionalidades

- Cadastro de usuários
- Login
- Autenticação com JWT
- Proteção de endpoints
- Cadastro de categorias
- Cadastro de transações
- Consulta de categorias
- Consulta de transações
- Validação de dados
- Documentação da API com Swagger

## Autenticação

A API utiliza JWT (JSON Web Token) para autenticação.

Após realizar o login, o usuário recebe um token que deve ser utilizado nos endpoints protegidos.

O token deve ser enviado no cabeçalho:

```text
Authorization: Bearer SEU_TOKEN

