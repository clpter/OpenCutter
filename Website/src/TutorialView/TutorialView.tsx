
import FileDownloadIcon from '@mui/icons-material/FileDownload';
import FileUploadIcon from '@mui/icons-material/FileUpload';
import { Box, Button, Container, Divider, Grid, Switch, Table, TableBody, TableCell, TableRow, ToggleButton, Typography } from "@mui/material";
import { saveAs } from "file-saver";
import { IosStyledNumberInput } from '../components/IosStyledNumberInput';
import MenuBarView from "../MenuBarView/MenuBarView";
import StyledPaper from '../components/StyledPaper';
import { useTranslation } from '../i18n/LanguageContext';

const TutorialView = () => {
  const { t } = useTranslation();

  const downloadList = () => {
    // Path to the file in the public folder
    const fileUrl = "/ExcampleList/MaterialListe.ods";  // file located in the public folder
    saveAs(fileUrl, "MaterialListe.ods"); // Downloads file as "example.txt"
  }

  const thicknessDataList = [{ data: "16" }, { data: "19" }, { data: "22" },];

  const content = [
    {
      example: <Button variant="contained" size="small" component="span" endIcon={<FileUploadIcon />}>
        {t.tutorial.importOdsButton}
      </Button>,
      explanation: <Box><Typography variant="body1" gutterBottom>{t.tutorial.importOdsExplanation}</Typography>
        <Button variant="contained" size="small" component="span" endIcon={<FileDownloadIcon />} onClick={downloadList}>
          {t.tutorial.downloadExample}
        </Button></Box>,
      reverse: false,
    },
    {
      example: <Box>{thicknessDataList.map((item, index) => (
        <ToggleButton key={index} value={1}>{item.data}</ToggleButton>
      ))}</Box>,
      explanation: <Box><Typography variant="body1" gutterBottom>{t.tutorial.thicknessExplanation}</Typography></Box>,
      reverse: true,
    },
    {
      example: <Button variant="contained">
        {t.tutorial.exportPdfButton}
      </Button>,
      explanation: <Box><Typography variant="body1" gutterBottom>{t.tutorial.exportPdfExplanation}</Typography></Box>,
      reverse: false,
    },
    {
      example:
        <StyledPaper sx={{
          marginX: {
            xs: 2,
            sm: 5,
            md: 15,
          },
          backgroundColor: "#A0BB75"
        }}>
          <Table>
            <TableBody>
              <TableRow >
                <TableCell ><label htmlFor="cuttingDirection">{t.tutorial.doNotRotateLabel} </label></TableCell>
                <TableCell ><Switch sx={{ m: 1 }} id="cuttingDirection" /></TableCell>
              </TableRow>
            </TableBody>
          </Table>
        </StyledPaper>,
      explanation:
        <Box>
          <Typography variant="body1" gutterBottom>{t.tutorial.doNotRotateExplanation}</Typography>
        </Box>,
      reverse: true,
    },
    {
      example:
        <StyledPaper sx={{
          marginX: {
            xs: 2,
            sm: 5,
            md: 15,
          },
          backgroundColor: "#A0BB75"
        }}>
          <Table>
            <TableBody>
              <TableRow>
                <TableCell ><label htmlFor="preferVerticalCuts">{t.tutorial.verticalCutsLabel} </label></TableCell>
                <TableCell ><Switch sx={{ m: 1 }} id="preferVerticalCuts" /></TableCell>
              </TableRow>
            </TableBody>
          </Table>
        </StyledPaper>,
      explanation:
        <Box>
          <Typography variant="body1" gutterBottom>{t.tutorial.verticalCutsExplanation}</Typography>
        </Box>,
      reverse: false,
    },
    {
      example:

        <StyledPaper sx={{
          marginX: {
            xs: 2,
            sm: 5,
            md: 15,
          },
          backgroundColor: "#A0BB75"
        }}>

          <Table>
            <TableBody>
              <TableRow>
                <TableCell >{t.tutorial.extraSizeLabel}</TableCell>
                <TableCell >
                  <IosStyledNumberInput
                    width="60px"
                    variant="outlined"
                    fullWidth
                    placeholder=""
                    type="number"
                    value={10}
                  />
                </TableCell>
              </TableRow>
            </TableBody>
          </Table>
        </StyledPaper>,
      explanation:
        <Box>
          <Typography variant="body1" gutterBottom>{t.tutorial.extraSizeExplanation}</Typography>
        </Box>,
      reverse: true,
    },

  ];

  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box >
        <MenuBarView header={t.tutorial.header} />
      </Box>

      <Container sx={{ py: 5 }}>
        {content.map((item, index) => (
          <Box key={index}>
            <Grid
              container
              spacing={4}
              direction={item.reverse ? "row-reverse" : "row"}
              sx={{ mb: 0, alignItems: "center" }}
            >
              <Grid size={{ xs: 12, md: 6 }}>
                <div>
                  {item.example}
                </div>
              </Grid>
              <Grid size={{ xs: 12, md: 6 }}>
                <StyledPaper elevation={20}>
                  {item.explanation}
                </StyledPaper>
              </Grid>
            </Grid>
            <Divider sx={{ width: '100%', my: 10 }} />
          </Box>
        ))}
      </Container>
    </Box>
  );

};
export default TutorialView;
