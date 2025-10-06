package fr.fellows.tp_test.v7;

import fr.fellows.tp_test.infrastructure.database.ConferenceEntity;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class ConferencePublishTest extends AbstractItTest {

    @Test
    void ajouterUneConference() {
        // Given
        stubPublish();
        ConferenceEntity conference = addConference();

        // When
        MvcResult result = doRequest(post("/api/v1/conferences/" + conference.getId() + "/publish"));

        // Then
        assertStatus(result, 200);
        assertBody(result, """
                {
                    "id": %d,
                    "nom":"Vive les tests",
                    "description":"la description",
                    "status":"PUBLIEE"
                }
                """.formatted(conference.getId()));
        assertThat(s3Template.objectExists("conference", conference.getId() + ".txt")).isTrue();
    }

    @Test
    void ajouterUneConferenceApiEnErreur() {
        // Given
        stubPublishEnErreur();
        ConferenceEntity conference = addConference();

        // When
        MvcResult result = doRequest(post("/api/v1/conferences/" + conference.getId() + "/publish"));

        // Then
        assertStatus(result, 500);
        assertBody(result, """
                {
                    "error":"Erreur inconnue"
                }
                """);
        assertThat(s3Template.objectExists("conference", conference.getId() + ".txt")).isTrue();
    }

    @Test
    void fg() {
        // Given
        supprimerBucket("conference");
        ConferenceEntity conference = addConference();

        // When
        MvcResult result = doRequest(post("/api/v1/conferences/" + conference.getId() + "/publish"));

        // Then
        assertStatus(result, 500);
        assertBody(result, """
                {
                    "error":"Erreur inconnue"
                }
                """);
    }

    @Test
    void fdfgdd() {
        // Given

        // When
        MvcResult result = doRequest(post("/api/v1/conferences/123/publish"));

        // Then
        assertStatus(result, 404);
        assertBody(result, """
                {
                    "error":"Ressource 123 introuvable"
                }
                """);

    }

}
