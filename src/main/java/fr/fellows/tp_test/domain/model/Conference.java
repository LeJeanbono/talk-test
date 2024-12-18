package fr.fellows.tp_test.domain.model;

public class Conference {

    private final Long id;

    private final String nom;

    private final String description;

    private StatusConference status;

    public Conference(Long id, String nom, String description, StatusConference status) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public StatusConference getStatus() {
        return status;
    }

    public void publish() {
        this.status = StatusConference.PUBLIEE;
    }

    public enum StatusConference {
        EN_REDACTION,
        PUBLIEE;
    }
}
