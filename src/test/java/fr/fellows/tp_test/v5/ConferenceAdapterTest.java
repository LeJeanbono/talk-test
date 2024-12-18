package fr.fellows.tp_test.v5;

import com.github.tomakehurst.wiremock.WireMockServer;
import fr.fellows.tp_test.TpTestApplication;
import fr.fellows.tp_test.domain.model.Conference;
import fr.fellows.tp_test.infrastructure.adapter.ConferenceAdapter;
import fr.fellows.tp_test.infrastructure.database.ConferenceEntity;
import fr.fellows.tp_test.infrastructure.database.ConferenceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@EnableWireMock(@ConfigureWireMock(
        baseUrlProperties = {"sessionize.base-url"}
))
class ConferenceAdapterTest {

    @Autowired
    ConferenceAdapter sut;

    @Autowired
    ConferenceRepository conferenceRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:latest");

    @InjectWireMock
    WireMockServer wireMock;

    @Test
    void rtrt() {
        // Given
        ConferenceEntity entity = new ConferenceEntity();
        entity.setNom("Vive les tests");
        entity.setDescription("la description");
        conferenceRepository.save(entity);

        // When
        Conference result = sut.recupererConference(entity.getId());

        // Then
        assertThat(result).usingRecursiveComparison().isEqualTo(new Conference(entity.getId(), "Vive les tests", "la description", null));
    }

    @Test
    void rryrey() {
        // Given
        Conference conference = new Conference(456L, "Vive les tests", "la description", Conference.StatusConference.EN_REDACTION);
        wireMock.stubFor(post("/api/talks")
                .withRequestBody(equalToJson("""
                        {
                            "nom": "Vive les tests",
                            "desc": "la description"
                        }
                        """))
                .willReturn(ok()));

        // When
        sut.publierConference(conference);

        // Then
    }

    @Test
    void main() {
        assertThrows(IllegalArgumentException.class, () -> TpTestApplication.main(null));
    }
}
