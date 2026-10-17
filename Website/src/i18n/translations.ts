const de = {
  nav: {
    openCut: "OpenCutter",
    tutorial: "Tutorial",
    navigator: "Navigation",
    language: "Sprache",
  },
  input: {
    piecesList: "Stückliste",
    stockList: "Bestandliste",
    nr: "Nr",
    length: "Länge",
    width: "Breite",
    height: "Höhe",
    count: "Anz",
    addRow: "Zeile hinzufügen",
    cutButton: "Zuschneiden",
    options: "Optionen",
    doNotRotate: "Stücke nicht drehen:",
    preferVerticalCuts: "Senkrechte Schnitte:",
    extraSize: "Zusatzgröße:",
    importOds: "Importiere Stücke als ods",
    panelSelection: "Plattenauswahl",
    unsupportedFile: "Nicht unterstütztes Dateiformat. Bitte laden Sie eine CSV- oder ODS-Datei hoch.",
  },
  info: {
    exportPdf: "EXPORT PDF",
    remainingPieces: "Übrige Stücke",
    wastePieces: "Rest Stücke",
  },
  svg: {
    stockSheet: "Bestand",
    id: "ID",
  },
  loading: {
    abort: "Abbrechen",
  },
  tutorial: {
    header: "Tutorial",
    importOdsButton: "Importiere Stücke als ods",
    importOdsExplanation: "Dieser Knopf erlaubt es eine Stückliste im folgenden Format hochzuladen:",
    downloadExample: "Download Beispiel",
    thicknessExplanation: "Wurde eine Materialliste erfolgreich importiert, so hat man die Möglichkeit über folgende Knöpfe, die Stücke nach Materialstärke sortiert auszuwählen.",
    exportPdfButton: "EXPORT PDF",
    exportPdfExplanation: "Mit den Knopf \"EXPORT PDF\" Lässt sich die zugeschnittene Ansicht als PDF exportieren",
    doNotRotateLabel: "Stücke nicht drehen:",
    doNotRotateExplanation: "\"Stücke nicht drehen\" bestimmt ob die Stücke gedreht werden dürfen oder nicht.",
    verticalCutsLabel: "Senkrechte Schnitte:",
    verticalCutsExplanation: "Ist \"Senkrechte Schnitte\" aktiviert, werden durchgehende, waagerechte Schnitte durch den Bestand deaktiviert.",
    extraSizeLabel: "Zusatzgröße:",
    extraSizeExplanation: "\"Zusatzgröße\" Vergrößert alle Stücke um den angegebenen Wert.",
  },
};

const en = {
  nav: {
    openCut: "OpenCutter",
    tutorial: "Tutorial",
    navigator: "Navigation",
    language: "Language",
  },
  input: {
    piecesList: "Parts List",
    stockList: "Stock List",
    nr: "No.",
    length: "Length",
    width: "Width",
    height: "Height",
    count: "Qty",
    addRow: "Add row",
    cutButton: "Calculate Cut",
    options: "Options",
    doNotRotate: "Do not rotate pieces:",
    preferVerticalCuts: "Vertical cuts:",
    extraSize: "Additional allowance:",
    importOds: "Import parts as ODS",
    panelSelection: "Thickness selection",
    unsupportedFile: "Unsupported file format. Please upload a CSV or ODS file.",
  },
  info: {
    exportPdf: "EXPORT PDF",
    remainingPieces: "Unplaced Parts",
    wastePieces: "Offcut Pieces",
  },
  svg: {
    stockSheet: "Stock Sheet",
    id: "ID",
  },
  loading: {
    abort: "Cancel",
  },
  tutorial: {
    header: "Tutorial",
    importOdsButton: "Import parts as ODS",
    importOdsExplanation: "This button allows you to upload a cut list in the following format:",
    downloadExample: "Download Example",
    thicknessExplanation: "Once a material list is successfully imported, you can use these buttons to select parts filtered by material thickness.",
    exportPdfButton: "EXPORT PDF",
    exportPdfExplanation: "With the \"EXPORT PDF\" button, the optimized cut layout can be exported as a PDF document.",
    doNotRotateLabel: "Do not rotate pieces:",
    doNotRotateExplanation: "\"Do not rotate pieces\" determines whether pieces can be rotated by 90° or must follow grain direction.",
    verticalCutsLabel: "Vertical cuts:",
    verticalCutsExplanation: "When \"Vertical cuts\" is enabled, continuous horizontal guillotine cuts across the stock sheet are avoided.",
    extraSizeLabel: "Additional allowance:",
    extraSizeExplanation: "\"Additional allowance\" increases all piece dimensions by the specified amount (saw kerf / margin).",
  },
};

export type TranslationSchema = typeof de;
export type Language = "de" | "en";
export type TranslationTree = TranslationSchema;

export const translations: Record<Language, TranslationSchema> = {
  de, en
};
