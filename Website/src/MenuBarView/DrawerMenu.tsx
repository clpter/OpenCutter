import CarpenterIcon from '@mui/icons-material/Carpenter';
import HelpCenterIcon from '@mui/icons-material/HelpCenter';
import TranslateIcon from '@mui/icons-material/Translate';
import { ListSubheader } from '@mui/material';
import Box from '@mui/material/Box';
import Divider from '@mui/material/Divider';
import Drawer from '@mui/material/Drawer';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import { useNavigate } from "react-router-dom";
import { useTranslation } from '../i18n/LanguageContext';

export default function DrawerMenu({ open, toggleDrawer }: { open: boolean, toggleDrawer: Function }) {
  const navigate = useNavigate(); // Initialize useNavigate hook
  const { t, language, setLanguage } = useTranslation();

  const handleNavigation = (path: string) => {
    navigate(path); // Navigate to the desired path
    toggleDrawer(false)(); // Close the drawer after navigation
  };

  const DrawerList = (
    <Box sx={{ width: 250 }} role="presentation" onClick={toggleDrawer(false)}>
      <List
        subheader={
          <ListSubheader component="div">
            {t.nav.navigator}
          </ListSubheader>
        }
      >
        <ListItem key={"Main"} disablePadding>
          <ListItemButton onClick={() => handleNavigation("/")}>
            <ListItemIcon>
              <CarpenterIcon />
            </ListItemIcon>
            <ListItemText primary={t.nav.openCut} />
          </ListItemButton>
        </ListItem>
      </List>
      <Divider />
      <List>
        <ListItem key={"Tutorial"} disablePadding>
          <ListItemButton onClick={() => handleNavigation("/tutorial")}>
            <ListItemIcon>
              <HelpCenterIcon />
            </ListItemIcon>
            <ListItemText primary={t.nav.tutorial} />
          </ListItemButton>
        </ListItem>
      </List>
      <Divider />
      <List
        subheader={
          <ListSubheader component="div">
            {t.nav.language}
          </ListSubheader>
        }
      >
        <ListItem disablePadding>
          <ListItemButton
            selected={language === "de"}
            onClick={(e) => {
              e.stopPropagation();
              setLanguage("de");
            }}
          >
            <ListItemIcon>
              <TranslateIcon />
            </ListItemIcon>
            <ListItemText primary="Deutsch" />
          </ListItemButton>
        </ListItem>
        <ListItem disablePadding>
          <ListItemButton
            selected={language === "en"}
            onClick={(e) => {
              e.stopPropagation();
              setLanguage("en");
            }}
          >
            <ListItemIcon>
              <TranslateIcon />
            </ListItemIcon>
            <ListItemText primary="English" />
          </ListItemButton>
        </ListItem>
      </List>
    </Box>
  );

  return (
    <Drawer anchor="left" open={open} onClose={toggleDrawer(false)}>
      {DrawerList}
    </Drawer>
  );
}
