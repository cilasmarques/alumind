# AluMind - Sistema de Gerenciamento de Feedback

AluMind é um sistema de gerenciamento de feedback para uma aplicação de saúde mental e bem-estar. Ele coleta, analisa e gera relatórios sobre os feedbacks dos usuários para ajudar a melhorar a aplicação.

## Funcionalidades

- **Análise de Feedback Baseada em IA:** Analisa automaticamente o feedback do usuário usando OpenAI
- **Classificação de Sentimento:** Categoriza o feedback como positivo, negativo ou inconclusivo
- **Detecção de Spam:** Filtra conteúdo spam ou inadequado
- **Extração de Solicitações de Funcionalidades:** Identifica funcionalidades solicitadas a partir do feedback dos usuários
- **Sistema de Relatórios:** Gera relatórios estatísticos sobre tendências de feedback
- **Relatórios Semanais Programados:** Envia automaticamente relatórios semanais por e-mail para os stakeholders

## Stack Tecnológica

- Java 17
- Spring Boot 3.4.4
- Spring AI (integração com OpenAI)
- Spring Data JPA
- PostgreSQL
- Lombok
- Maven

## Pré-requisitos

- Java 17 ou superior
- Maven
- Banco de dados PostgreSQL
- Docker (opcional, para implantação em contêineres)

## Configuração

### Configuração do Banco de Dados

Configure a conexão com seu banco de dados no arquivo `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/alumindDB
spring.datasource.username=alumindUser
spring.datasource.password=alumindSecret
```

Você pode substituir essas configurações com variáveis de ambiente.

### Configuração da API OpenAI

Defina sua chave de API do OpenAI no arquivo `application.properties`:

```properties
spring.ai.openai.api-key=sua-chave-api
```

Por questões de segurança, é recomendado usar variáveis de ambiente em vez de inserir a chave diretamente no código.

### Configuração de E-mail

Configure as configurações de e-mail para envio de relatórios:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=seu-email@exemplo.com
spring.mail.password=sua-senha-de-email
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Destinatários de E-mail
email.weekly-report.recipients=stakeholders@exemplo.com
```

## Compilação e Execução

### Executando com Maven

```bash
./mvnw spring-boot:run
```

### Gerando um JAR

```bash
./mvnw clean package
```

### Executando com Docker Compose

```bash
docker-compose up -d
```

### Buildando e executando com Docker Compose

```bash
docker-compose up --build
```


## Endpoints da API

### Gerenciamento de Feedback

- `POST /feedbacks` - Submeter novo feedback
  - Corpo da requisição: `{"feedback": "Texto do feedback do usuário"}`
  - Retorna: Análise de sentimento e solicitações de funcionalidades identificadas

- `GET /feedbacks/{id}` - Obter informações detalhadas do feedback por ID

### Relatórios

- `GET /reports` - Gerar e recuperar um relatório atual de feedback

- `GET /reports/sendEmail` - **[PENSADO APENAS PARA TESTES]** Simula o envio de um relatório semanal por e-mail utilizando a data atual como referência. Este endpoint é destinado exclusivamente para testes.

## Arquitetura

- **Controllers:** Gerenciam requisições e respostas HTTP
- **Services:** Contêm a lógica de negócio para análise de feedback e relatórios
- **Repositories:** Gerenciam a persistência de dados
- **Models:** Definem entidades do banco de dados
- **DTOs:** Objetos de Transferência de Dados para requisições e respostas da API
- **Scheduler:** Gerencia tarefas agendadas como relatórios semanais
- **Utils:** Classes utilitárias incluindo prompts para LLM

## Tarefas Agendadas

A aplicação inclui uma tarefa agendada que executa todo domingo às 8:00 para gerar e enviar relatórios semanais de feedback por e-mail para os stakeholders.

## Contribuindo

Ao contribuir para este projeto, siga estas diretrizes:

1. Faça um fork do repositório
2. Crie um branch para a funcionalidade (`git checkout -b feature/funcionalidade-incrivel`)
3. Faça commit das suas alterações (`git commit -m 'feat: Adiciona funcionalidade incrível'`)
4. Faça push para o branch (`git push origin feature/funcionalidade-incrivel`)
5. Abra um Pull Request