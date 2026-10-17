import React from "react";
import { ToggleButton, ToggleButtonGroup } from "@mui/material";
import { useTranslation } from "./LanguageContext";
import { Language } from "./translations";

export const LanguageSwitcher: React.FC = () => {
  const { language, setLanguage } = useTranslation();

  const handleLanguageChange = (
    _event: React.MouseEvent<HTMLElement>,
    newLanguage: Language | null
  ) => {
    if (newLanguage !== null) {
      setLanguage(newLanguage);
    }
  };

  return (
    <ToggleButtonGroup
      value={language}
      exclusive
      onChange={handleLanguageChange}
      size="small"
      aria-label="Language selection"
      sx={{
        backgroundColor: "rgba(0, 0, 0, 0.08)",
        borderRadius: "16px",
        height: "32px",
        "& .MuiToggleButton-root": {
          px: 1.5,
          py: 0,
          fontWeight: "bold",
          fontSize: "0.8rem",
          border: "none",
          borderRadius: "16px",
          color: "inherit",
          "&.Mui-selected": {
            backgroundColor: "primary.dark",
            color: "#fff",
            "&:hover": {
              backgroundColor: "primary.dark",
            },
          },
        },
      }}
    >
      <ToggleButton value="de" aria-label="German">
        DE
      </ToggleButton>
      <ToggleButton value="en" aria-label="English">
        EN
      </ToggleButton>
    </ToggleButtonGroup>
  );
};
