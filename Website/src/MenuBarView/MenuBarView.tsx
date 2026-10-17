import MenuIcon from '@mui/icons-material/Menu';
import { AppBar, IconButton, Toolbar, Typography } from '@mui/material';
import { useState } from 'react';
import DrawerMenu from './DrawerMenu';
import { LanguageSwitcher } from '../i18n/LanguageSwitcher';


const MenuBarView = ({ header }: { header: string }) => {
  const [open, setOpen] = useState(false);
  const toggleDrawer = (state: boolean) => () => {
    setOpen(state);
  };

  return (
    <div>
      <AppBar position="static">
        <Toolbar>
          <IconButton
            size="large"
            edge="start"
            color="inherit"
            aria-label="menu"
            sx={{ mr: 2 }}
            onClick={toggleDrawer(true)}
          >
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            {header}
          </Typography>
          <LanguageSwitcher />
        </Toolbar>
      </AppBar>
      <DrawerMenu open={open} toggleDrawer={toggleDrawer} />
    </div>
  );

};
export default MenuBarView;
