# DevPilot

DevPilot is an AI-powered coding companion that connects to your GitHub repositories and lets you chat with your own codebase. Sign in with GitHub, sync a repo, and ask questions — DevPilot retrieves the most relevant code and streams back context-aware answers to help you understand, debug, and build your project faster.

## Features

- **GitHub OAuth 2.0 login** — secure sign-in and repo access via your GitHub account
- **Repository sync** — pull in a repo's source files for indexing
- **Retrieval-Augmented Generation (RAG) pipeline** — code is chunked, embedded, and stored in a vector database for fast semantic search
- **Context-aware chat** — ask questions about your codebase and get answers grounded in the actual source, with inline code citations
- **Real-time streaming responses** — answers stream back live via Server-Sent Events (SSE), rendered as streaming markdown
- **Chat sessions** — organize conversations per repository

## How It Works

```
GitHub Repository
      ↓
Repository Sync
      ↓
Code Files
      ↓
Chunking / Indexing
      ↓
Embedding API
      ↓
Vector Database (pgvector)
      ↓
User asks a question
      ↓
Similarity Search
      ↓
Relevant Code Context
      ↓
LLM / GenAI API
      ↓
Streaming Response (SSE)
```

1. A user connects their GitHub account and selects a repository to sync.
2. DevPilot pulls the repo's files and splits them into manageable chunks.
3. Each chunk is converted into a vector embedding and stored in **pgvector** (PostgreSQL).
4. When the user asks a question, DevPilot embeds the query and runs a similarity search against the indexed code.
5. The most relevant chunks are retrieved and passed to an LLM as context.
6. The LLM's response is streamed back to the frontend in real time over SSE and rendered as markdown, with citations pointing back to the source files.

## Tech Stack

**Frontend**
- Next.js
- Streaming markdown rendering
- Server-Sent Events (SSE) client for live responses

**Backend**
- Spring Boot
- Spring Data JPA
- GitHub OAuth 2.0
- REST APIs
- SSE for streaming chat responses

**AI / Data**
- Retrieval-Augmented Generation (RAG) pipeline
- Code chunking & indexing
- Vector embeddings
- **pgvector** (PostgreSQL) for vector storage and similarity search
- LLM API for generating chat responses

## Getting Started

### Prerequisites

- Java 17+ and Maven
- Node.js 18+
- PostgreSQL with the [`pgvector`](https://github.com/pgvector/pgvector) extension enabled
- A GitHub OAuth App (Client ID & Secret)
- An API key for your chosen LLM / embedding provider

### 1. Clone the repository

```bash
git clone https://github.com/GauravSahu891/devpilot.git
cd devpilot
```

### 2. Configure environment variables

Create a `.env` file (frontend) and `application.properties` / `application.yml` (backend) with values such as:

```
# GitHub OAuth
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret
GITHUB_REDIRECT_URI=http://localhost:8080/login/oauth2/code/github

# Database (PostgreSQL + pgvector)
DB_URL=jdbc:postgresql://localhost:5432/devpilot
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# LLM / Embedding provider
LLM_API_KEY=your_llm_api_key
EMBEDDING_API_KEY=your_embedding_api_key
```

> Replace the placeholder values above with your actual credentials. Never commit real secrets to version control.

### 3. Set up the database

```sql
CREATE EXTENSION IF NOT EXISTS vector;
```

Run any migration scripts / JPA schema generation to create the required tables.

### 4. Run the backend

```bash
cd backend
mvn spring-boot:run
```

### 5. Run the frontend

```bash
cd frontend
npm install
npm run dev
```

The app should now be running at `http://localhost:3000` (frontend) with the API served from `http://localhost:8080` (backend).

## Usage

1. Open the app and sign in with GitHub.
2. Select a repository to sync — DevPilot will index its source files.
3. Once indexing is complete, open the chat and ask questions about the repo (e.g. *"Where is user authentication handled?"* or *"Explain how the payment flow works."*).
4. Responses stream in live, with citations linking back to the relevant files.

## Project Structure

```
devpilot/
├── backend/          # Spring Boot API, GitHub OAuth, RAG pipeline, SSE chat
├── frontend/          # Next.js app, chat UI, auth flow
└── README.md
```

## Roadmap

- [ ] Support for multiple LLM providers
- [ ] Multi-repo cross-referencing in a single chat session
- [ ] Deployment guide (Docker / CI-CD)
- [ ] Team/workspace support

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

## Author

**Gaurav Sahu**
[GitHub](https://github.com/GauravSahu891) · [LinkedIn](https://linkedin.com/in/gaurav-s-75073b316)
