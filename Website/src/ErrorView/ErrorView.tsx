import { Alert, Container } from "@mui/material";

const ErrorView = ({ errorMessage }: { errorMessage: String }) => {
    return (
        <Container style={{ textAlign: 'center' }}>
            {<Alert severity="warning">{errorMessage}</Alert>}
        </Container>
    );
}
export default ErrorView;