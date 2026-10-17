import { AppBar, Box, Drawer, Fab, IconButton, Toolbar, Typography, useMediaQuery } from '@mui/material';
import { useRef, useState } from 'react';
import InputView from '../InputView/InputView';
import SVGView from '../SvgView/SVGView';
import TuneIcon from '@mui/icons-material/Tune';
import AssessmentIcon from '@mui/icons-material/Assessment';
import axios from 'axios';
import ErrorView from '../ErrorView/ErrorView';
import InfoView from '../InfoView/InfoView';
import LoadingIndicatorView from '../LoadingIndicator/LoadingIndicatorView';
import MenuBarView from '../MenuBarView/MenuBarView';
import { ApiResponse, CutOptions, InputRectangle, ListRectangeRow } from '../types/rectangle';
import { useTranslation } from '../i18n/LanguageContext';
import { useTheme } from '@mui/material/styles';
import CloseIcon from '@mui/icons-material/Close';

const MainView = () => {
  const { t, language } = useTranslation();

  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  // Input list rows
  const [piecesList, setPiecesList] = useState<ListRectangeRow[]>([{ id: '1', height: '', width: '', count: '' }]);
  const [stockList, setStockList] = useState<ListRectangeRow[]>([{ id: '1', height: '', width: '', count: '' }]);

  // Grouped cutting configuration options
  const [options, setOptions] = useState<CutOptions>({
    addend: 0,
    cuttingDirection: true,
    preferVerticalCuts: false,
    selectedValue: { thickness: "", data: [] },
    thicknessDataList: [],
  });

  // Atomic cutting API result
  const [cutResult, setCutResult] = useState<ApiResponse | null>(null);
  const svgRef = useRef<HTMLDivElement>(null);

  // Request & Execution status
  const [loading, setLoading] = useState(false);
  const [abortController, setAbortController] = useState<AbortController | null>(null);
  const [errorMessage, setErrorMessage] = useState("");

  // Call Cutting API
  const handleRectanglesUpdate = async (piecesData: InputRectangle[], stockData: InputRectangle[]) => {
    const controller = new AbortController();
    setAbortController(controller);

    setLoading(true);
    setErrorMessage("");

    try {
      const httpClient = axios.create({
        baseURL: import.meta.env.VITE_API_BASE_URL || "/api"
      });

      const api = options.cuttingDirection ? "/cut_1" : "/cut_2";
      const request = {
        pieces: piecesData,
        stock: stockData,
        cuttingDirection: options.cuttingDirection,
        preferVerticalCuts: options.preferVerticalCuts,
      };

      const response = await httpClient.post<ApiResponse>(api, request, { signal: controller.signal });
      setCutResult(response.data);
    } catch (exception: any) {
      if (axios.isCancel(exception)) {
        console.log('Request canceled', exception.message);
      } else {
        setErrorMessage(exception.response?.data || "An error occurred while calculating cuts.");
        console.error("Error fetching data", exception);
      }
      setCutResult(null);
    } finally {
      setLoading(false);
    }
  };

  const onAbort = () => {
    if (abortController) {
      abortController.abort();
      setLoading(false);
    }
  };

  const [openInfo, setOpenInfo] = useState(false);
  const [openInput, setOpenInput] = useState(false);

  return (
    <Box sx={{ flexGrow: 1, bgcolor: 'background.default' }}>
      {isMobile ? (
        <Box sx={{ height: '100dvh', width: '100vw' }}>
          <MenuBarView header={t.nav.openCut} />


          <Box sx={{ flex: 1, overflowY: 'auto', overflowX: 'hidden', display: 'flex', justifyContent: 'center', px: 0.5, py: 1, pb: 10 }}>
            <div className="center">
              {loading && <LoadingIndicatorView onAbort={onAbort} />}
              {!loading && (
                <SVGView
                  stockList={cutResult?.stock ?? []}
                  placedPiecesList={cutResult?.placedPieces ?? []}
                  svgRef={svgRef}
                />
              )}
              {errorMessage && <ErrorView errorMessage={errorMessage} />}
            </div>
          </Box>

          {/* Button Links: Eingabe */}
          <Fab
            color="primary"
            variant="extended"
            onClick={() => setOpenInput(true)}
            sx={{
              position: 'fixed',
              bottom: 24,
              left: 16,
              zIndex: 1000,
              boxShadow: 4,
              fontWeight: 'bold',
            }}
          >
            <TuneIcon sx={{ mr: 1 }} />
            {language === 'de' ? 'Eingabe' : 'Input'}
          </Fab>

          {/* Button Rechts: Info */}
          <Fab
            color="secondary"
            variant="extended"
            onClick={() => setOpenInfo(true)}
            sx={{
              position: 'fixed',
              bottom: 24,
              right: 16,
              zIndex: 1000,
              boxShadow: 4,
              fontWeight: 'bold',
            }}
          >
            <AssessmentIcon sx={{ mr: 1 }} />
            {language === 'de' ? 'Info' : 'Info'}
          </Fab>

          {/* Dialog Links: Eingabe */}
          <Drawer anchor='left' open={openInput} onClose={() => setOpenInput(false)}>
            <AppBar
              sx={{
                position: 'relative',
              }}
            >
              <Toolbar>
                <IconButton edge="start" color="inherit" onClick={() => setOpenInput(false)} aria-label="close">
                  <CloseIcon />
                </IconButton>
                <Typography sx={{ ml: 2, flex: 1, fontWeight: 'bold' }} variant="h6" component="div">
                  {t.input.piecesList}
                </Typography>
              </Toolbar>
            </AppBar>
            <Box sx={{ p: 2, bgcolor: 'background.default', flex: 1, overflowY: 'auto' }}>
              <InputView
                onRectanglesUpdate={(pieces, stock) => {
                  setOpenInput(false);
                  handleRectanglesUpdate(pieces, stock);
                }}
                piecesList={piecesList}
                setPiecesList={setPiecesList}
                stockList={stockList}
                setStockList={setStockList}
                options={options}
                setOptions={setOptions}
              />
            </Box>
          </Drawer>

          {/* Dialog Rechts: Info */}
          <Drawer anchor='right' open={openInfo} onClose={() => setOpenInfo(false)}>
            <AppBar
              sx={{
                position: 'relative',
              }}
            >
              <Toolbar>
                <IconButton edge="start" color="inherit" onClick={() => setOpenInfo(false)} aria-label="close">
                  <CloseIcon />
                </IconButton>
                <Typography sx={{ ml: 2, flex: 1, fontWeight: 'bold' }} variant="h6" component="div">
                  {language === 'de' ? 'Ergebnis & Info' : 'Results & Info'}
                </Typography>
              </Toolbar>
            </AppBar>
            <Box sx={{ p: 2, bgcolor: 'background.default', flex: 1, overflowY: 'auto' }}>
              <InfoView
                pieces={cutResult?.pieces ?? []}
                waste={cutResult?.waste ?? []}
                svgRef={svgRef}
              />
            </Box>
          </Drawer>
        </Box>
      ) : (
        <div className="border-pane">
          <div className='top'>
            <MenuBarView header={t.nav.openCut} />
          </div>
          <div className="left">
            <InputView
              onRectanglesUpdate={handleRectanglesUpdate}
              piecesList={piecesList}
              setPiecesList={setPiecesList}
              stockList={stockList}
              setStockList={setStockList}
              options={options}
              setOptions={setOptions}
            />
          </div>
          <div className="center">
            {loading && <LoadingIndicatorView onAbort={onAbort} />}
            {!loading && (
              <SVGView
                stockList={cutResult?.stock ?? []}
                placedPiecesList={cutResult?.placedPieces ?? []}
                svgRef={svgRef}
              />
            )}
            {errorMessage && <ErrorView errorMessage={errorMessage} />}
          </div>
          <div className="right">
            <InfoView
              pieces={cutResult?.pieces ?? []}
              waste={cutResult?.waste ?? []}
              svgRef={svgRef}
            />
          </div>
        </div>
      )}
    </Box>
  );
};

export default MainView;
