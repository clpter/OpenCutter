import { createTheme, Theme } from '@mui/material/styles';

export const theme: Theme = createTheme({
  palette: {
    primary: {
      main: '#A0BB75',
      light: '#C5D6AB',
      dark: '#5F7944',
      contrastText: '#000',
    },
    secondary: {
      main: '#BBB375',
      light: '#E3DF91',
      dark: '#807968',
      contrastText: '#000',
    },
    info: {
      main: '#CFC87D',
    },
    warning: {
      main: '#FF0000',
      light: '#E66D66',
    },
    background: {
      default: '#F5F3D3',
      paper: '#BBB375',
    },
  },
  typography: {
    body1: {
      fontSize: '1.1rem',
      lineHeight: 1.6,
      color: '#1a1a1a',
      fontWeight: 400,
      fontFamily: '"Roboto", sans-serif',
    },
    body2: {
      fontSize: '1.1rem',
      lineHeight: 1.6,
      color: '#1a1a1a',
      fontWeight: 400,
      fontFamily: '"Roboto", sans-serif',
      textAlign: 'left',
    },
    h2: {
      fontFamily: 'Roboto, sans-serif',
      fontSize: '2rem',
      fontWeight: 600,
      lineHeight: 1.2,
    },
    h3: {
      fontFamily: 'Roboto, sans-serif',
      fontSize: '1.5rem',
      fontWeight: 600,
      lineHeight: 2,
    },
    h4: {
      fontFamily: 'Roboto, sans-serif',
      fontSize: '1.2rem',
      fontWeight: 500,
      lineHeight: 1.2,
    },
    h5: {
      fontFamily: 'Roboto, sans-serif',
      fontSize: '1.0rem',
      fontWeight: 400,
      lineHeight: 1,
    },
  },
  components: {
    MuiSwitch: {
      defaultProps: {
        disableRipple: true,
      },
      styleOverrides: {
        root: ({ theme }) => ({
          width: 42,
          height: 26,
          padding: 0,
          '& .MuiSwitch-switchBase': {
            padding: 0,
            margin: 2,
            transitionDuration: '300ms',
            '&.Mui-checked': {
              transform: 'translateX(16px)',
              color: '#fff',
              '& + .MuiSwitch-track': {
                backgroundColor: theme.palette.primary.dark,
                opacity: 1,
                border: 0,
              },
              '&.Mui-disabled + .MuiSwitch-track': {
                opacity: 0.5,
              },
            },
            '&.Mui-focusVisible .MuiSwitch-thumb': {
              color: '#33cf4d',
              border: '6px solid #fff',
            },
            '&.Mui-disabled .MuiSwitch-thumb': {
              color: theme.palette.grey[100],
            },
            '&.Mui-disabled + .MuiSwitch-track': {
              opacity: 0.7,
            },
          },
          '& .MuiSwitch-thumb': {
            boxSizing: 'border-box',
            width: 22,
            height: 22,
          },
          '& .MuiSwitch-track': {
            borderRadius: 26 / 2,
            backgroundColor: '#E9E9EA',
            opacity: 1,
            transition: theme.transitions.create(['background-color'], {
              duration: 500,
            }),
          },
        }),
      },
    },
    MuiToggleButton: {
      styleOverrides: {
        root: ({ theme }) => ({
          borderRadius: '8px',
          border: '1px solid #000',
          padding: '10px 20px',
          textTransform: 'none',
          color: theme.palette.primary.contrastText,
          '&.Mui-selected': {
            backgroundColor: theme.palette.primary.dark,
            color: theme.palette.primary.contrastText,
            '&:hover': {
              backgroundColor: theme.palette.primary.main,
            },
          },
          '&:hover': {
            backgroundColor: theme.palette.primary.main,
          },
        }),
      },
    },
  },
});
