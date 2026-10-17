import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import FileUploadIcon from '@mui/icons-material/FileUpload';
import { Switch, Table, TableBody, TableCell, TableRow, ToggleButton, ToggleButtonGroup, Typography } from "@mui/material";
import Accordion from '@mui/material/Accordion';
import AccordionDetails from '@mui/material/AccordionDetails';
import AccordionSummary from '@mui/material/AccordionSummary';
import Button from '@mui/material/Button';
import Papa from 'papaparse';
import * as XLSX from 'xlsx';
import '../App.css';
import { CutOptions, ListRectangeRow } from '../types/rectangle';
import { SavedThickness } from '../types/savedThickness';
import { IosStyledNumberInput } from '../components/IosStyledNumberInput';
import { useTranslation } from '../i18n/LanguageContext';

interface InputOptionsViewProps {
  options: CutOptions;
  setOptions: React.Dispatch<React.SetStateAction<CutOptions>>;
  onThicknessPresetChanged: (data: ListRectangeRow[]) => void;
}

const InputOptionsView = ({
  options,
  setOptions,
  onThicknessPresetChanged,
}: InputOptionsViewProps) => {
  const { t } = useTranslation();

  const onAddedChangedInternal = (event: React.ChangeEvent<HTMLInputElement>) => {
    const val = event.target.value;
    setOptions((prev) => ({ ...prev, addend: val === '' ? 0 : parseInt(val, 10) || 0 }));
  };

  const handleFileUpload = async (event: any) => {
    const file = event.target.files[0];

    if (file) {
      const fileExtension = file.name.split('.').pop().toLowerCase();

      if (fileExtension === 'csv') {
        handleCsvFile(file);
      } else if (fileExtension === 'ods') {
        await handleOdsFile(file);
      } else {
        alert(t.input.unsupportedFile);
      }
    }
  };

  const handleCsvFile = (file: any) => {
    Papa.parse(file, {
      header: false,
      complete: (result) => {
        const filteredData = result.data.filter((row: any) => (!isNaN(parseInt(row[0])) && !isNaN(parseInt(row[6]))));
        const thicknesses: number[] = filteredData.map((row: any) => (
          parseInt(row[6])
        ));

        const uniqueThicknesses: number[] = Array.from(new Set(thicknesses));

        let savedData: SavedThickness[] = [];
        uniqueThicknesses.forEach((item: number) => {
          const data = filteredData.filter((row: any) => parseInt(row[6]) === item)
            .map((row: any) => ({
              id: row[0],
              count: row[3],
              width: row[4],
              height: row[5],
            } as ListRectangeRow));

          savedData = [...savedData, { thickness: String(item), data: data }];
        });

        setOptions((prev) => ({ ...prev, thicknessDataList: savedData }));
      },
    });
  };

  const handleOdsFile = async (file: any) => {
    const reader = new FileReader();
    reader.onload = async (e) => {
      const data = new Uint8Array(e.target?.result as ArrayBuffer);
      const workbook = XLSX.read(data, { type: 'array' });
      const sheet = workbook.Sheets['Materialliste']; // Assuming first sheet
      const csv = XLSX.utils.sheet_to_csv(sheet);

      handleCsvFile(csv);
    };
    reader.readAsArrayBuffer(file);
  };

  const handleToggleChange = (newThickness: SavedThickness) => {
    if (newThickness !== null) {
      onThicknessPresetChanged(newThickness.data);
      setOptions((prev) => ({ ...prev, selectedValue: newThickness }));
    }
  };

  const onCuttingDirectionChanged = () => {
    setOptions((prev) => ({ ...prev, cuttingDirection: !prev.cuttingDirection }));
  };

  const onPreferVerticalCutsChanged = () => {
    setOptions((prev) => ({ ...prev, preferVerticalCuts: !prev.preferVerticalCuts }));
  };

  return (
    <Accordion defaultExpanded>
      <AccordionSummary
        expandIcon={<ExpandMoreIcon />}
        aria-controls="panel3-content"
        id="panel3-header"
      >
        <Typography variant="h4">{t.input.options}</Typography>
      </AccordionSummary>
      <AccordionDetails>
        <Table>
          <TableBody>
            <TableRow>
              <TableCell sx={{ padding: '16px' }}><label htmlFor="cuttingDirection"><Typography variant='h5' >{t.input.doNotRotate}</Typography> </label></TableCell>
              <TableCell sx={{ padding: '16px' }}><Switch sx={{ m: 1 }} id="cuttingDirection" checked={options.cuttingDirection} onChange={onCuttingDirectionChanged} /></TableCell>
            </TableRow>
            <TableRow>
              <TableCell sx={{ padding: '16px' }}><label htmlFor="preferVerticalCuts"><Typography variant='h5' >{t.input.preferVerticalCuts}</Typography> </label></TableCell>
              <TableCell sx={{ padding: '16px' }}><Switch sx={{ m: 1 }} id="preferVerticalCuts" checked={options.preferVerticalCuts} onChange={onPreferVerticalCutsChanged} /></TableCell>
            </TableRow>
            <TableRow>
              <TableCell sx={{ padding: '16px' }}><Typography variant='h5' >{t.input.extraSize}</Typography></TableCell>
              <TableCell sx={{ padding: '16px' }}><IosStyledNumberInput
                width="60px"
                variant="outlined"
                fullWidth
                placeholder="0"
                type="number"
                value={options.addend === 0 ? '' : options.addend}
                onChange={onAddedChangedInternal}
              /></TableCell>
            </TableRow>
            <TableRow>
              <TableCell colSpan={2}>
                <input type="file" accept=".ods, .csv" id="file-upload" onChange={handleFileUpload} style={{ display: 'none' }} />
                <label htmlFor="file-upload">
                  <Button variant="contained" size="small" component="span" endIcon={<FileUploadIcon />}>
                    {t.input.importOds}
                  </Button>
                </label>
              </TableCell>
            </TableRow>
            {options.thicknessDataList.length > 0 && (
              <TableRow>
                <TableCell colSpan={2}>
                  {t.input.panelSelection}<br />
                  <ToggleButtonGroup
                    color="primary"
                    value={options.selectedValue}
                    exclusive
                    onChange={(e, value: SavedThickness) => handleToggleChange(value)}
                    aria-label="Platform"
                  >
                    {options.thicknessDataList.map((item, index) => (
                      <ToggleButton key={index} value={item}>{item.thickness}</ToggleButton>
                    ))}
                  </ToggleButtonGroup>
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </AccordionDetails>
    </Accordion>
  );
};

export default InputOptionsView;
