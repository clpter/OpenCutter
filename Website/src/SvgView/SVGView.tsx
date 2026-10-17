import { RefObject } from 'react';
import { theme } from '../theme/theme';
import { ApiRectangle } from "../types/rectangle";
import { useTranslation } from "../i18n/LanguageContext";
import { Typography, useMediaQuery } from '@mui/material';

const SVGView = ({ stockList, placedPiecesList, svgRef }: { stockList: ApiRectangle[], placedPiecesList: ApiRectangle[], svgRef: RefObject<HTMLDivElement | null> }) => {
  const { t } = useTranslation();

  const isMobile = useMediaQuery(theme.breakpoints.down('md'));

  return (
    <div ref={svgRef}>
      {stockList.map(stockItem => {
        // Calculate screen size
        let screenWidth;
        let screenHeight;
        let baseFontSize; // Base font size

        if (isMobile) {
          screenWidth = window.innerWidth - 30;
          screenHeight = window.innerHeight - 200 - 60;
          baseFontSize = 30;
        } else {
          screenWidth = window.innerWidth - 350 - 200 - 20;
          screenHeight = window.innerHeight - 200 - 32;
          baseFontSize = 40;
        }

        // Calculate the square that uses maximum area
        const svgSize = Math.min(screenWidth, screenHeight);

        const aspectRatio = stockItem.width / stockItem.height;
        let svgWidth = screenWidth;
        let svgHeight = screenWidth / aspectRatio;

        if (svgHeight > screenHeight) {
          svgHeight = screenHeight;
          svgWidth = svgHeight * aspectRatio;
        }

        // Calculate scaled font size

        const scaleFactor = svgSize / Math.max(stockItem.width, stockItem.height); // Scaling factor
        const scaledFontSize = baseFontSize / scaleFactor;

        return (
          <div key={-stockItem.id} className='svgDIV' style={{ marginBottom: '32px' }}>
            <div>
              <Typography variant="h3">{t.svg.stockSheet}: {stockItem.number} {t.svg.id}: {stockItem.id + 1}</Typography>
              {stockItem.width} x {stockItem.height}

            </div>
            <div>

              <svg width={svgWidth} height={svgHeight} viewBox={`0 0 ${stockItem.width} ${stockItem.height}`}>

                <rect key="background" height={stockItem.height} width={stockItem.width} fill={theme.palette.primary.dark} strokeWidth={0} />

                {placedPiecesList.filter(position => position.assignment === stockItem.id).map(position => (

                  <g key={position.id}>{/* g-Tag is for grouping of SVG Elements*/}

                    <rect key={position.id} id={position.id.toString()} x={position.x} y={position.y} width={position.width} height={position.height} fill={theme.palette.secondary.light} stroke={theme.palette.warning.main} strokeWidth={scaledFontSize / 10} />
                    <text
                      key={position.id + 2000}
                      x={position.x + position.width / 2}
                      y={position.y + position.height / 2}
                      dominantBaseline="middle"
                      textAnchor="middle"
                      fontSize={scaledFontSize}
                    >
                      {position.number}
                    </text>
                    <text
                      key={position.id + 2001}
                      x={position.x + position.width / 2}
                      y={position.y + 13 / scaleFactor}
                      dominantBaseline="middle"
                      textAnchor="middle"
                      fontSize={scaledFontSize / 2.5}
                    >
                      {position.width}
                    </text>
                    <text
                      key={position.id + 2002}
                      x={position.x + 13 / scaleFactor}
                      y={position.y + position.height / 2}
                      dominantBaseline="middle"
                      textAnchor="middle"
                      fontSize={scaledFontSize / 2.5}
                      transform={`rotate(-90, ${position.x + 13 / scaleFactor}, ${position.y + position.height / 2})`}

                    >
                      {position.height}
                    </text>

                  </g>
                ))}

                <rect key="border" height={stockItem.height} width={stockItem.width} fill="transparent" stroke="blue" strokeWidth={scaledFontSize / 10} />
              </svg>
            </div>
          </div>
        );
      })}
      <div></div>
    </div>
  )
}

export default SVGView;
