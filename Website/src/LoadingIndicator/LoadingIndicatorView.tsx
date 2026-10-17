import { Button, Container, LinearProgress } from "@mui/material";
import { useTranslation } from "../i18n/LanguageContext";

const LoadingIndicatorView = ({ onAbort }: { onAbort: () => void }) => {
  const { t } = useTranslation();
  return (
    <Container style={{ textAlign: 'center' }}>
      <div>

        <LinearProgress color="primary" style={{ width: "100%" }} />
        <Button variant="contained" color="error" sx={{ marginTop: 2 }} onClick={onAbort}>{t.loading.abort}</Button>
      </div>

    </Container>
  );
}

export default LoadingIndicatorView;
