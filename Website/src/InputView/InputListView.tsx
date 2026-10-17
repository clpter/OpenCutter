import DeleteForeverIcon from '@mui/icons-material/DeleteForever';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { IconButton, Typography } from "@mui/material";
import Accordion from '@mui/material/Accordion';
import AccordionDetails from '@mui/material/AccordionDetails';
import AccordionSummary from '@mui/material/AccordionSummary';
import Button from '@mui/material/Button';
import '../App.css';
import { StyledTable, StyledTableDefinition, StyledTableHeader, StyledTableRow } from '../components/StyledTable';
import { ListRectangeRow } from '../types/rectangle';
import { StyledInput } from '../components/StyledInput';
import { useTranslation } from '../i18n/LanguageContext';


const InputListView = ({ header, items, onItemChanged }: {
  header: string;
  items: ListRectangeRow[];
  onItemChanged: (newItems: ListRectangeRow[]) => void;
}) => {
  const { t } = useTranslation();
  const updateItemField = (item: ListRectangeRow, field: 'width' | 'height' | 'count', value: string) => {
    const updatedItem = { ...item, [field]: value };

    let updatedData = items.map((row) => (row.id === item.id ? updatedItem : row));

    const hasIncompleteRow = updatedData.some(
      (row) => row.count === '' || row.height === '' || row.width === ''
    );
    if (!hasIncompleteRow) {
      const nextId = items.length > 0 ? Number(items[items.length - 1].id) + 1 : 1;
      updatedData = [...updatedData, { id: String(nextId), height: '', width: '', count: '' }];
    }

    onItemChanged(updatedData);
  };

  const onAddItem = () => {
    const nextId = items.length > 0 ? Number(items[items.length - 1].id) + 1 : 1;
    onItemChanged([...items, { id: String(nextId), height: '', width: '', count: '' }]);
  };

  const onDeleteItem = (indexToDelete: number) => {
    onItemChanged(items.filter((_, index) => index !== indexToDelete));
  };

  return (
    <Accordion defaultExpanded>
      <AccordionSummary
        expandIcon={<ExpandMoreIcon />}
        aria-controls="panel1-content"
        id="panel1-header"
      >
        <Typography variant="h3">{header}</Typography>
      </AccordionSummary>
      <AccordionDetails>
        <StyledTable>
          <thead>
            <tr>
              <StyledTableHeader>{t.input.nr}</StyledTableHeader>
              <StyledTableHeader>{t.input.length}</StyledTableHeader>
              <StyledTableHeader>{t.input.width}</StyledTableHeader>
              <StyledTableHeader>{t.input.count}</StyledTableHeader>
            </tr>
          </thead>
          <tbody>
            {items.map((item, index) => (
              <StyledTableRow key={item.id} $index={index}>
                <StyledTableDefinition>
                  {item.id}
                </StyledTableDefinition>
                <StyledTableDefinition>
                  <StyledInput
                    type="number"
                    name="width"
                    value={item.width}
                    onChange={(e) => updateItemField(item, 'width', e.target.value)}
                  />
                </StyledTableDefinition>
                <StyledTableDefinition>
                  <StyledInput
                    type="number"
                    name="height"
                    value={item.height}
                    onChange={(e) => updateItemField(item, 'height', e.target.value)}
                  />
                </StyledTableDefinition>
                <StyledTableDefinition>
                  <StyledInput
                    type="number"
                    name="count"
                    value={item.count}
                    onChange={(e) => updateItemField(item, 'count', e.target.value)}
                  />
                </StyledTableDefinition>
                <StyledTableDefinition >
                  <IconButton color="warning" tabIndex={-1} aria-label="delete" size="small" onClick={() => onDeleteItem(index)} sx={{ padding: 0 }}>
                    <DeleteForeverIcon fontSize="small" />
                  </IconButton>
                </StyledTableDefinition>
              </StyledTableRow>
            ))}
          </tbody>
        </StyledTable>
        <Button variant="contained" size="small" onClick={onAddItem} sx={{ mt: 2 }}>
          {t.input.addRow}
        </Button>
      </AccordionDetails>
    </Accordion>
  );
};

export default InputListView;
