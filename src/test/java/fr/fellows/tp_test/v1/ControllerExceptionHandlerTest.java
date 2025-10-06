package fr.fellows.tp_test.v1;

import fr.fellows.tp_test.application.ControllerExceptionHandler;
import fr.fellows.tp_test.domain.exception.RessourceNonTrouveeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ControllerExceptionHandlerTest {

    @InjectMocks
    ControllerExceptionHandler sut;

    @Test
    void doitGererRessourceNonTrouveeException() {
        // Given
        RessourceNonTrouveeException ex = new RessourceNonTrouveeException(123L);

        // When
        ControllerExceptionHandler.ErrorMessage result = sut.resourceNotFoundException(ex);

        // Then
        assertThat(result).isEqualTo(new ControllerExceptionHandler.ErrorMessage("Ressource 123 introuvable"));
    }

    @Test
    void doitGererErreurInconnueException() {
        // Given
        // When
        ControllerExceptionHandler.ErrorMessage result = sut.erreurInconnueException();

        // Then
        assertThat(result).isEqualTo(new ControllerExceptionHandler.ErrorMessage("Erreur inconnue"));
    }
}
