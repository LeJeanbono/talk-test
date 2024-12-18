package fr.fellows.tp_test.application;

import fr.fellows.tp_test.domain.exception.RessourceNonTrouveeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(RessourceNonTrouveeException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage resourceNotFoundException(RessourceNonTrouveeException ex) {
        return new ErrorMessage("Ressource " + ex.getId() + " introuvable");
    }

    public record ErrorMessage(String error) {
    }

}
