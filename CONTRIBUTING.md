# Contributing to OpenCutter

First off, thank you for considering contributing to **OpenCutter**! Open source
projects thrive when people like you step up to help.

Whether you are fixing a bug, improving the packing algorithm, refining the UI,
or writing documentation, all contributions are warmly welcomed.

---

## Getting Started

### 1. Fork & Clone

1. Fork the repository on GitHub.
2. Clone your fork locally:

   ```bash
   git clone https://github.com/<your-username>/opencutter.git
   cd opencutter
   ```

3. Create a feature branch off `main`:

   ```bash
   git checkout -b feature/my-new-feature
   # or
   git checkout -b fix/issue-description
   ```

### 2. Prerequisites

- **Java Development Kit (JDK) 21+**
- **Apache Maven 3.9+**
- **Node.js 20+** and **npm**

### 3. Running the Backend (JAVA Spring-Boot API)

```bash
cd API

# Start the Spring Boot application (default: 127.0.0.1:8080)
mvn spring-boot:run
```

For more details on backend architecture and tests, see
[API/README.md](API/README.md).

### 4. Running the Frontend (Vite)

```bash
cd Website

# Install dependencies
npm install

# Start Vite dev server with hot reload (port 3000)
npm run dev
```

During local development, requests to `/api` are automatically proxied to
`http://localhost:8080` by Vite.

For more details see [Website/README.md](Website/README.md).

---

## Project Architecture & Rules

OpenCutter is organized with two independent components:

```text
├── API/       # Spring Boot REST API (Guillotine cut optimization)
└── Website/   # React + TypeScript + Vite (Frontend)
```

**Naming Conventions:** All new files, components, props, and functions must be
written in English.

---

## Pull Request Guidelines

1. **Keep PRs focused:** One bug fix or one feature per pull request. Avoid
   large monolithic PRs that touch unrelated code.
2. **Commit Messages:** Follow
   [Conventional Commits](https://www.conventionalcommits.org/):
   - `feat: add export to DXF format`
   - `fix: prevent off-by-one error in kerf margin calculation`
   - `docs: update self-hosting instructions`
   - `refactor: extract CutConfigContext in frontend`
3. **Run CI Checks Locally:**
   - Backend: `cd API && mvn clean test`
   - Frontend: `cd Website && npm run build`
4. **Submit PR:** Push your branch to your fork and submit a PR to `main`. Fill
   out the PR template completely.

---

## Reporting Bugs & Suggesting Enhancements

- **Bugs:** Open an issue using the
  [Bug Report](.github/ISSUE_TEMPLATE/bug_report.md) template. Please include
  steps to reproduce, sample sheet/pieces dimensions, browser or OS version, and
  console/terminal logs.
- **Feature Requests:** Open an issue using the
  [Feature Request](.github/ISSUE_TEMPLATE/feature_request.md) template
  detailing the motivation and proposed behavior.
