Sistema Backend Aluno Online
Sobre o Projeto

O Sistema Backend Aluno Online é uma API REST desenvolvida com Java e Spring Boot para gerenciamento de alunos, professores, disciplinas e matrículas.

O sistema permite:

- Cadastrar alunos
- Cadastrar professores
- Cadastrar disciplinas
- Realizar matrículas
- Atualizar notas
- Aprovar ou reprovar alunos automaticamente
- Trancar matrículas
- Emitir histórico acadêmico

---

Tecnologias Utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Web
- PostgreSQL
- Maven
- Lombok
- Insomnia
- DBeaver

---

Arquitetura

O projeto foi desenvolvido utilizando arquitetura em camadas:

### Controller
Responsável por receber as requisições HTTP.

### Service
Responsável pelas regras de negócio da aplicação.

### Repository
Responsável pelo acesso ao banco de dados utilizando JPA.

### Model
Representa as entidades do sistema.

### DTO
Utilizado para transferência de dados entre as camadas.

---

## Entidades

### Aluno
- id
- nome
- email
- cpf

### Professor
- id
- nome
- email
- cpf

### Disciplina
- id
- nome
- cargaHoraria
- professor

### MatriculaAluno
- id
- aluno
- disciplina
- nota1
- nota2
- status

---

## Regras de Negócio

### Matrícula
Ao criar uma matrícula o status inicial é:

MATRICULADO

### Atualização de Notas

A média é calculada pela fórmula:

(nota1 + nota2) / 2

### Aprovação

Média maior ou igual a 7.0:

APROVADO

Média menor que 7.0:

REPROVADO

### Trancamento

Uma matrícula só pode ser trancada quando estiver com status MATRICULADO.

---

## Endpoints

### Alunos

- POST /alunos
- GET /alunos
- GET /alunos/{id}
- PUT /alunos/{id}
- DELETE /alunos/{id}

### Professores

- POST /professores
- GET /professores
- GET /professores/{id}
- PUT /professores/{id}
- DELETE /professores/{id}

### Disciplinas

- POST /disciplinas
- GET /disciplinas
- GET /disciplinas/{id}
- PUT /disciplinas/{id}
- DELETE /disciplinas/{id}

### Matrículas

- POST /matriculas
- PATCH /matriculas/trancar/{id}
- PATCH /matriculas/atualizar-notas/{id}
- GET /matriculas/emitir-historico/{alunoId}
