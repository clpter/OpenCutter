import { Box, Button } from '@mui/material';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';
import { RefObject } from 'react';
import { ApiRectangle } from '../types/rectangle';
import InfoWasteView from './InfoWasteView';
import InfoPieceView from './InfoPieceView';
import { useTranslation } from '../i18n/LanguageContext';

const InfoView = ({ pieces, waste, svgRef }: { pieces: ApiRectangle[], waste: ApiRectangle[], svgRef: RefObject<HTMLDivElement | null> }) => {
    const { t } = useTranslation();
    const exportAsPdf = () => {
        const svgContainer = svgRef.current; // Access the Container-Div

        if (svgContainer) {
            const svgDivs = Array.from(svgContainer.getElementsByClassName("svgDIV")); // Get all svgs of the container
            const pdf = new jsPDF();
            pdf.deletePage(1);

            const generatePDF = async () => {
                let yOffset: number;

                for (let i = 0; i < svgDivs.length; i++) {
                    const item = svgDivs[i];

                    if (i % 2 === 0) {
                        pdf.addPage('a4', 'portrait'); //a4: 210mm x 297mm
                        yOffset = 10;
                    } else {
                        yOffset = 153;
                    }

                    if (item instanceof HTMLDivElement) {
                        const canvas = await html2canvas(item);
                        // Get the width and height of the canvas before converting to image data
                        const canvasWidth = canvas.width;
                        const canvasHeight = canvas.height;
                        // Calculate the aspect ratio
                        const aspectRatio = canvasWidth / canvasHeight;
                        const imgData = canvas.toDataURL('image/png');
                        let imgWidth = 190; // 10mm Margin
                        let imgHeight = imgWidth / aspectRatio;

                        // Check if the height exceeds the A4 page height
                        if (imgHeight > 133) { // Allow for space on the page (e.g., 10mm margin)
                            imgHeight = 133; // Set height to half of A4 page height with margin
                            imgWidth = imgHeight * aspectRatio; // Recalculate width to maintain aspect ratio
                        }

                        // Add the image to the PDF
                        pdf.addImage(imgData, 'JPEG', 10, yOffset, imgWidth, imgHeight);

                    }
                }
                pdf.save("download.pdf"); // Save the PDF after processing all items
            };
            generatePDF();
        };
    }

    return (
        <>
            <Box sx={{ my: 2 }}>
                <Button variant="contained" onClick={exportAsPdf}>
                    {t.info.exportPdf}
                </Button>
            </Box>
            <Box>
                <InfoPieceView header={t.info.remainingPieces} data={pieces} />
                <InfoWasteView header={t.info.wastePieces} data={waste} />
            </Box>
        </>
    );
};

export default InfoView;