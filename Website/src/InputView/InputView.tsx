import { Box, Button } from "@mui/material";
import '../App.css';
import { CutOptions, InputRectangle, ListRectangeRow } from '../types/rectangle';
import InputListView from './InputListView';
import InputOptionsView from "./InputOptionsView";
import { useTranslation } from '../i18n/LanguageContext';

interface InputViewProps {
  onRectanglesUpdate: (piecesData: InputRectangle[], stockData: InputRectangle[]) => void;
  piecesList: ListRectangeRow[];
  setPiecesList: (data: ListRectangeRow[]) => void;
  stockList: ListRectangeRow[];
  setStockList: (data: ListRectangeRow[]) => void;
  options: CutOptions;
  setOptions: React.Dispatch<React.SetStateAction<CutOptions>>;
}

const InputView = ({
  onRectanglesUpdate,
  piecesList,
  setPiecesList,
  stockList,
  setStockList,
  options,
  setOptions,
}: InputViewProps) => {
  const { t } = useTranslation();

  const getInputRectanglesFromListitems = (input: ListRectangeRow[], valueToAdd: number = 0): InputRectangle[] => {
    const tempItems: InputRectangle[] = [];
    let id = 0;

    input
      .filter((item) => item.height !== '' && item.width !== '' && item.count !== '')
      .forEach((item) => {
        for (let i = 0; i < Number.parseInt(item.count); i++) {
          tempItems.push({
            id: id++,
            width: Number.parseInt(item.width) + valueToAdd,
            height: Number.parseInt(item.height) + valueToAdd,
            number: Number.parseInt(item.id),
          });
        }
      });

    return tempItems;
  };

  const handleSubmit = () => {
    const piecesData = getInputRectanglesFromListitems(piecesList, options.addend);
    const stockData = getInputRectanglesFromListitems(stockList);

    onRectanglesUpdate(piecesData, stockData);
  };

  const onThicknessPresetChanged = (data: ListRectangeRow[]) => {
    setPiecesList(data);
  };

  return (

    <Box
      sx={{
        maxWidth: '100%',
        boxSizing: 'border-box',
        display: 'flex',
        flexDirection: 'column',
        gap: 1.5,
      }}
    >

      <InputListView header={t.input.piecesList} items={piecesList} onItemChanged={setPiecesList} />
      <InputListView header={t.input.stockList} items={stockList} onItemChanged={setStockList} />

      <InputOptionsView
        options={options}
        setOptions={setOptions}
        onThicknessPresetChanged={onThicknessPresetChanged}
      />

      <Button variant="contained" onClick={handleSubmit} sx={{ mt: 2 }}>
        {t.input.cutButton}
      </Button>
    </Box>
  );
};

export default InputView;
