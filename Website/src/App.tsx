import { Route, Routes } from "react-router-dom";
import './App.css';
import MainView from './MainView/MainView';
import TutorialView from './TutorialView/TutorialView';
import { ThemeProvider } from "@mui/material/styles";
import { theme } from "./theme/theme";
import { CssBaseline } from "@mui/material";
import { LanguageProvider } from "./i18n/LanguageContext";

export default function App() {

  return (
    <LanguageProvider>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <div className="App">
          <Routes>
            <Route path="/" element={<MainView />} />
            <Route path="tutorial" element={<TutorialView />} />
          </Routes>
        </div>
      </ThemeProvider>
    </LanguageProvider>
  );
};
