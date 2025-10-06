package fr.fellows.tp_test.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Conference {

    private final Long id;
    private final String nom;
    private final String description;
    private StatusConference status;

    public void publish() {
        this.status = StatusConference.PUBLIEE;
    }

    public enum StatusConference {
        EN_REDACTION,
        PUBLIEE
    }
}
