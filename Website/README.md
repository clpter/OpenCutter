# OpenCutter Web Frontend

Modern single-page application (SPA) for the OpenCutter 2D guillotine cutting
optimizer, built with **React 19**, **TypeScript**, **Vite**, and **Material UI
(MUI)**.

---

## Tech Stack

- **Framework:** React 19 & TypeScript
- **Bundler & Dev Server:** [Vite](https://vite.dev/)
- **UI & Design System:** [Material UI (MUI v9)](https://mui.com/) & Emotion
- **Networking:** Axios
- **Spreadsheet Parsing:** SheetJS (`xlsx`) and PapaParse (CSV)
- **Document Export:** jsPDF & html2canvas

---

## Frontend Guidelines (`/Website`)

- **Type Safety:** Define TypeScript types/interfaces in `src/types/` rather
  than using `any`.
- **Styling:** Use Material UI (MUI) components. When appropriate define
  reusable components located in `Website/src/components/`
- **Verification:** Ensure TypeScript passes compilation and builds cleanly:

  ```bash
  cd Website
  npm run build
  ```

---

## Getting Started

### Prerequisites

- **Node.js 20+**
- **npm** (comes with Node.js)

### Installation

```bash
cd Website
npm install
```

### Environment Configuration

By default, the frontend sends API requests to `/api` (relative path).

- In development (`npm run dev`), Vite automatically proxies `/api` to
  `http://localhost:8080`.
- In Docker, Nginx automatically proxies `/api` to the backend container.

If you are hosting the frontend independently on a separate server or CDN (e.g.
Cloudflare Pages) and need it to communicate with a remote API instance:

```bash
VITE_API_BASE_URL=https://api.yourdomain.com/api npm run build
```

---

## Available Scripts

In the `Website` directory, you can run:

### `npm start`

Starts the local development server.  
Open [http://localhost:3000](http://localhost:3000) to view it in your browser.

### `npm run build`

Runs TypeScript type-checking (`tsc`) and compiles optimized production assets
into the `build/` directory.

### `npm run preview`

Locally previews the production build generated in `build/`.

### `npm test`

_(coming soon)_

---

## Project Structure

```text
src/
├── MainView/            # Central coordinator: application state, API calls, error handling
├── InputView/           # Input tables for target pieces, stock sheets, and cut settings
├── SvgView/             # Interactive 2D SVG canvas rendering sheet layouts and cuts
├── InfoView/            # Optimization metrics, unplaced pieces list, and PDF export
├── TutorialView/        # In-app interactive tutorial and sample dataset loader
├── MenuBarView/         # Navigation toolbar, app title, and drawer menu
├── types/               # TypeScript interfaces (Rectangle, CuttingList, CutOptions)
├── theme/               # Material UI theme overrides and typography
├── App.tsx              # Root component & layout router
└── index.tsx            # Vite DOM mount entry point
```

---

## Docker Deployment

The frontend includes a multi-stage `Dockerfile` which compiles the React app
using Node and serves the static assets via a lightweight Nginx container. The
`nginx.conf` file defines, that incomming requests to `/api` are proxied to
`127.0.0.1/api`.

---

## License

This component is part of the OpenCutter project and is licensed under the
[GNU General Public License v3.0](../LICENSE).
