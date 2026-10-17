# 📋 OpenCut Roadmap & TODO

This document tracks planned refactoring, clean architecture improvements, and feature tasks for the **OpenCut** project.

---

## 🌐 1. Multilingual Support (i18n: German & English)

- [ ] **Setup i18n framework:** Install and configure `react-i18next` and `i18next` (or lightweight JSON translation dictionaries).
- [ ] **Extract all hardcoded UI strings into translation files (`de.json`, `en.json`):**
  - [ ] **Menu & Navigation:** `DrawerMenu.tsx`, `MenuBarView.tsx` ("Tutorial", "OpenCut", etc.).
  - [ ] **Input Forms & Options:** `InputOptionsView.tsx`
    - "Stücke nicht drehen" ➔ "Do not rotate pieces"
    - "Senkrechte Schnitte" ➔ "Prefer vertical cuts"
    - "Zusatzgröße" ➔ "Kerf / Saw blade margin"
    - "Importiere Stücke als ods" ➔ "Import piece list (.ods/.csv)"
    - "Plattenauswahl" ➔ "Sheet thickness selection"
  - [ ] **List Tables:** `InputListView.tsx`
    - "Stückliste" ➔ "Cut List / Target Pieces"
    - "Bestandliste" ➔ "Stock Sheets"
    - "Zeile hinzufügen" ➔ "Add row"
    - Table headers: "Nr" ➔ "No.", "Breite" ➔ "Width", "Höhe" ➔ "Height", "Anz" ➔ "Qty"
  - [ ] **Result Views:** `InfoView.tsx`, `InfoPiecesView.tsx`, `InfoWasteView.tsx`
    - "Übrige Stücke" ➔ "Unplaced Pieces"
    - "Rest Stücke" ➔ "Offcuts / Waste"
    - "EXPORT PDF" ➔ "Export PDF"
  - [ ] **Tutorial:** `TutorialView.tsx` (translate all tutorial guide steps and explanations).
- [ ] **Add Language Selector Component:** Add a clean DE / EN toggle in the `MenuBarView` toolbar.
- [ ] **Auto-detection:** Automatically detect the user's browser language on first load (`navigator.language`).

---

## 🧹 2. Frontend Codebase: Clean English Refactoring

Remove legacy German names and typos in code artifacts (variables, parameters, files, types) to follow international software engineering conventions:

- [ ] **MainView (`Website/src/MainView/MainView.tsx`):**
  - [ ] `bestandList, setBestandList` ➔ `stockList, setStockList`
  - [ ] `positionenList, setPositionenList` ➔ `positionsList, setPositionsList`
  - [ ] `restStücke, setRestStücke` ➔ `remainingPieces, setRemainingPieces`
  - [ ] `reste, setReste` ➔ `wastePieces, setWastePieces`
  - [ ] Function parameters: `(stückData, bestandData)` ➔ `(piecesData, stockData)`
- [ ] **InputView (`Website/src/InputView/InputView.tsx`):**
  - [ ] Parameter and local variables `stückData, bestandData` ➔ `piecesData, stockData`
- [ ] **InfoView (`Website/src/InfoView/InfoView.tsx`):**
  - [ ] Props `({ stücke, reste, svgRef })` ➔ `({ remainingPieces, wastePieces, svgRef })`
- [ ] **SVGView (`Website/src/SvgView/SVGView.tsx`):**
  - [ ] Props `bestandList, positionenList` ➔ `stockList, positionsList`
  - [ ] Loop variable `bestandItem` ➔ `stockItem`
- [ ] **File & Component Names:**
  - [ ] Rename `InfoStueckView.tsx` ➔ `InfoPiecesView.tsx`
  - [ ] Rename `InfoRestView.tsx` ➔ `InfoWasteView.tsx`
  - [ ] Rename folder `MenueBarView/` ➔ `MenuBarView/`
  - [ ] Rename file `MenueBarView.tsx` ➔ `MenuBarView.tsx`
  - [ ] Rename file `DrawerMenue.tsx` ➔ `DrawerMenu.tsx`
  - [ ] Rename public folder `Website/public/ExcampleList` ➔ `Website/public/ExampleList`
- [ ] **Typo Fixes:**
  - [ ] `Website/src/types/rectangle.ts`: Fix typo `ListRectangeRow` ➔ `ListRectangleRow` (missing 'l').
  - [ ] `InputOptionsView.tsx`: Fix function typo `onCuttingDirectonChanged` ➔ `onCuttingDirectionChanged`.
  - [ ] `TutorialView.tsx`: Fix typo `"Downlowd Beispiel"` ➔ `"Download Beispiel"`.

---

## ☕ 3. Backend (Spring Boot) Architecture Improvements

- [ ] **Remove Duplicate `@ExceptionHandler` in `CuttingController.java`:**
  - The local handlers shadow `GlobalExceptionHandler.java` (making it dead code). Delete the local `@ExceptionHandler` methods from the controller so errors are handled uniformly by the advice.
- [ ] **Replace `System.out.println` with SLF4J Logger:**
  - In `GlobalExceptionHandler.java`, replace `System.out.println(...)` with `private static final Logger log = LoggerFactory.getLogger(...)` and use `log.warn(...)`.
- [ ] **Invert Layer Dependency Violation:**
  - `CuttingService.java` (application layer) imports `com.cuttingAPI.web.CustomException` (web layer).
  - Move `CustomException` to `com.cuttingAPI.application.exception.CuttingCalculationException` (or similar).
  - Change exception to extend `RuntimeException` instead of checked `Exception` so controller and service methods don't need `throws Exception`.
- [ ] **Configurable CORS:**
  - Replace hardcoded `@CrossOrigin(origins = "*")` with a property-driven allowed origins list (`cors.allowed-origins`).

---

## ⚡ 4. Frontend Architecture & Bundle Optimization

- [ ] **Eliminate Dual CSS-in-JS Engines:**
  - Currently the app installs and uses **both** `styled-components` and `@emotion/styled` (used by MUI).
  - Migrate the small custom tables in `style/styledComponents.ts` and `InfoView` to MUI's `styled` or standard MUI `<Table>` components.
  - Uninstall `styled-components` and `@types/styled-components` to reduce bundle size.
- [ ] **Fix CSS Syntax Error in `App.css`:**
  - Fix unescaped comment on line 5 (`---------------- Borderpane CSS ---------------- html,`).
- [ ] **Reduce Prop Drilling:**
  - `MainView.tsx` currently passes 15 props down through `InputView.tsx` into `InputOptionsView.tsx`.
  - Refactor using a React Context (`CutConfigContext`) or custom state hook.
- [ ] **Bundle Size & Code-Splitting:**
  - Implement React `lazy()` and dynamic imports for `TutorialView` and PDF export libraries (`jsPDF`, `html2canvas`, `xlsx`) so initial page load stays under 500 kB.

---

## 📦 5. Open Source & Community Readiness

- [x] **Add `LICENSE` File:** GNU General Public License v3.0 (GPL-3.0) added to repository root.
- [x] **Root `README.md`:**
  - [x] Elevator pitch with badges (build status, Docker pulls, license, Java 21, React 19).
  - [ ] Screenshots / GIF demo of the visual cutting layout (`docs/assets/opencut-hero-screenshot.png`).
  - [x] Quickstart guide for self-hosters (`docker compose -f deploy/compose.yaml up -d`).
  - [x] Quickstart guide for developers (Java 21, Vite).
  - [x] Web GUI workflow and REST API curl usage.
- [x] **Subproject Documentation:**
  - [x] `API/README.md` updated with Java 21, Docker instructions, configuration envs.
  - [x] `Website/README.md` completely rewritten for React 19, TypeScript, and Vite.
- [x] **Add `CONTRIBUTING.md`:** Guide for setting up Java 21 and Node 20 locally, running unit tests, and opening PRs.
- [x] **GitHub Templates:** Added `.github/ISSUE_TEMPLATE/bug_report.md`, `feature_request.md`, and `.github/PULL_REQUEST_TEMPLATE.md`.
