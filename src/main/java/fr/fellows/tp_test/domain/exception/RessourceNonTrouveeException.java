package fr.fellows.tp_test.domain.exception;

import lombok.Getter;

@Getter
public class RessourceNonTrouveeException extends RuntimeException {

    private Long id;

    public RessourceNonTrouveeException(Long id) {
        super();
        this.id = id;
    }

}
