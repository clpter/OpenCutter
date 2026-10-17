import { styled } from '@mui/material/styles';


export const StyledTable = styled('table')({
  width: '100%',
  borderCollapse: 'collapse',
});

export const StyledTableHeader = styled('th')(({ theme }) => ({
  padding: '4px 8px',
  ...theme.typography.h5
}));

export const StyledTableRow = styled('tr', {
  shouldForwardProp: (prop) => prop !== '$index',
})<{ $index?: number }>(({ theme, $index = 0 }) => ({
  backgroundColor: $index % 2 === 1 ? theme.palette.secondary.main : theme.palette.info.main,
}));

export const StyledTableDefinition = styled('td')(({ theme }) => ({
  borderRight: `1px solid ${theme.palette.secondary.contrastText}`,
}));

