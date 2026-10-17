import { TextField } from '@mui/material';
import { styled } from '@mui/material/styles';

export const IosStyledNumberInput = styled(TextField)<{ width?: string }>(({ width = '60px', theme }) => ({
  width: width,
  '& .MuiOutlinedInput-root': {
    borderRadius: '12px',
    backgroundColor: '#F9F9F9',
    boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)',
    '& fieldset': {
      borderColor: '#E0E0E0',
    },
    '&:hover fieldset': {
      borderColor: '#C0C0C0',
    },
    '&.Mui-focused fieldset': {
      borderColor: theme.palette.primary.dark,
      borderWidth: '2px',
    },
  },
  '& .MuiInputBase-input': {
    padding: '10px 14px',
    appearance: 'textfield',
    fontSize: '16px',
    '&::-webkit-outer-spin-button, &::-webkit-inner-spin-button': {
      margin: 0,
    },
  },
}));

export default IosStyledNumberInput;
