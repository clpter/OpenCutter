import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { Accordion, AccordionDetails, AccordionSummary, Typography } from '@mui/material';
import { StyledTable, StyledTableDefinition, StyledTableHeader, StyledTableRow } from '../components/StyledTable';
import { ApiRectangle } from '../types/rectangle';
import { useTranslation } from '../i18n/LanguageContext';


const InfoWasteView = ({ header, data }: { header: string, data: ApiRectangle[] }) => {
  const { t } = useTranslation();
  return (
    <Accordion defaultExpanded>
      <AccordionSummary
        expandIcon={<ExpandMoreIcon />}
        aria-controls="panel1-content"
        id="panel3-header">
        <Typography variant="h4">{header}</Typography>
      </AccordionSummary>
      <AccordionDetails>
        <StyledTable>
          <thead>
            <tr>
              <StyledTableHeader>{t.input.nr}</StyledTableHeader>
              <StyledTableHeader>{t.input.width}</StyledTableHeader>
              <StyledTableHeader>{t.input.height}</StyledTableHeader>
            </tr>
          </thead>
          <tbody>
            {data.map((rect, index) => (
              <StyledTableRow key={index} $index={index}>
                <StyledTableDefinition>
                  {(rect.assignment ?? 0) + 1}
                </StyledTableDefinition>
                <StyledTableDefinition>
                  {rect.width}
                </StyledTableDefinition>
                <StyledTableDefinition>
                  {rect.height}
                </StyledTableDefinition>
              </StyledTableRow>
            ))}
          </tbody>
        </StyledTable>
      </AccordionDetails>
    </Accordion>
  );
}

export default InfoWasteView;
