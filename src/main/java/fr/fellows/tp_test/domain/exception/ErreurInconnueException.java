package fr.fellows.tp_test.domain.exception;

import lombok.Getter;

@Getter
public class ErreurInconnueException extends RuntimeException {

    private final Exception exception;

    public ErreurInconnueException(Exception e) {
        super();
        this.exception = e;
    }

}
