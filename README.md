#
 # StockBrain
Sua Inteligência de Mercado. Uma plataforma Fullstack que une a análise fundamentalista clássica com o poder da Inteligência Artificial Generativa para democratizar decisões de investimento.
# Sobre o Projeto
O StockBrain é uma aplicação robusta de análise financeira. O sistema resolve a dor de ter que consultar múltiplas fontes para analisar uma ação. Ele busca cotações e dividendos automaticamente (Brasil e EUA), calcula o Preço Teto (Método Décio Bazin) e usa o Google Gemini para interpretar os dados e gerar um relatório textual sobre os riscos e oportunidades do ativo.

Além da análise, o sistema conta com um Gerenciador de Carteira completo e um Simulador de Juros Compostos para projeção de liberdade financeira.

# Funcionalidades Principais
 1. Análise Híbrida (Matemática + IA)
Cálculo Bazin: Verifica automaticamente se o Dividend Yield é atrativo (>6%) e sugere o Preço Teto.

Consultor IA: Integração com o Google Gemini 2.0 Flash que atua como um analista sênior, explicando os fundamentos da ação em linguagem natural baseada nos dados técnicos coletados.

 2. Backend Resiliente (Circuit Breaker)
Multi-API: Conecta-se à Brapi (Ações BR) e Alpha Vantage (Ações EUA).

Fallback Inteligente: Se a API brasileira falha ou bloqueia por limite de plano gratuito, o sistema automaticamente recorre à API internacional para tentar salvar a análise, garantindo alta disponibilidade.

 3. Carteira & Dashboard
Gráficos Animados: Acompanhamento visual da alocação da carteira (Pizza) e histórico de rentabilidade comparada ao CDI (Barras) usando Chart.js.

Preço Médio: Cálculo automático do preço médio de compra a cada novo aporte.

Monitoramento: Botão para ativar o "Robô de Alerta" que notifica por e-mail quando o preço atinge o alvo.

 4. Simulação de Futuro
Projeta o patrimônio acumulado com base nos juros compostos, aportes mensais e o valor atual da carteira real do usuário.

#  Tecnologias Utilizadas
Backend (API)
Java 17 & Spring Boot 3 (Web, Security, Data JPA, Validation)

Spring Session JDBC (Gerenciamento de Sessão Distribuída no Banco)

RestClient (Cliente HTTP Moderno para consumo de APIs externas)

PostgreSQL (Banco de Dados Relacional)

JUnit 5 & Mockito (Testes unitários para regras de negócio e resiliência)

Java Mail Sender (Envio de Notificações)

Frontend (Web)
Angular 17+ (Standalone Components)

TypeScript & RxJS

Bootstrap 5 & SCSS (Estilização responsiva e moderna)

Chart.js (Visualização de dados)

 Como Rodar o Projeto
Pré-requisitos
Java JDK 17+

Node.js (v18+)

PostgreSQL instalado e rodando

Chaves de API (Gratuitas):

Brapi (Dados Brasil)

Alpha Vantage (Dados EUA/Backup)

Google AI Studio (Inteligência Artificial)

# Configuração do Banco de Dados
Crie um banco de dados no seu PostgreSQL:

SQL

CREATE DATABASE stock_brain;
 Configuração do Backend
Clone o repositório:

Bash

git clone https://github.com/SEU_USUARIO/stock-brain.git
Entre na pasta do backend (stock-brain) e abra o arquivo src/main/resources/application.yaml.

Configure suas credenciais:

YAML

spring:
  datasource:
    password: "SUA_SENHA_DO_POSTGRES"
  mail:
    password: "SUA_SENHA_DE_APP_DO_GMAIL" # Senha de App de 16 dígitos

stock-brain:
  alpha-vantage:
    api-key: "SUA_KEY_ALPHA"
  brapi:
    token: "SUA_KEY_BRAPI"
  gemini:
    api-key: "SUA_KEY_GOOGLE"
Rode a aplicação:

Bash

./mvnw spring-boot:run
 Configuração do Frontend
Entre na pasta do frontend (stock-brain-front):

Bash

cd stock-brain-front
Instale as dependências:

Bash

npm install
Rode o servidor de desenvolvimento:

Bash

ng serve
Acesse http://localhost:4200 no seu navegador.

# Testes
O projeto possui uma suíte de testes unitários robusta para garantir a lógica financeira e a resiliência das APIs.

Bash

# Rodar testes do backend
cd stock-brain
./mvnw test
  Contribuição
Contribuições são bem-vindas!

# Faça um Fork do projeto.

Crie uma Branch para sua Feature (git checkout -b feature/NovaFeature).

Faça o Commit (git commit -m 'Add: Nova Feature').

Faça o Push (git push origin feature/NovaFeature).

Abra um Pull Request.

# Licença
Este projeto está sob a licença MIT.

Desenvolvido com ☕ e código limpo.
