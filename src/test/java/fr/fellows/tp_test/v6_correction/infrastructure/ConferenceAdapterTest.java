package fr.fellows.tp_test.v6_correction.infrastructure;

import com.github.tomakehurst.wiremock.WireMockServer;
import fr.fellows.tp_test.domain.exception.ErreurInconnueException;
import fr.fellows.tp_test.domain.model.Conference;
import fr.fellows.tp_test.infrastructure.adapter.ConferenceAdapter;
import fr.fellows.tp_test.infrastructure.database.ConferenceEntity;
import fr.fellows.tp_test.infrastructure.database.ConferenceRepository;
import io.awspring.cloud.s3.S3Template;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpServerErrorException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@EnableWireMock(@ConfigureWireMock(
        baseUrlProperties = {"sessionize.base-url"}
))
class ConferenceAdapterTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:latest");

    @ServiceConnection
    static LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.4"))
            .withServices(LocalStackContainer.Service.S3);

    @InjectWireMock
    WireMockServer wireMock;

    @Autowired
    ConferenceAdapter sut;

    @Autowired
    ConferenceRepository conferenceRepository;

    @Autowired
    S3Template s3Template;

    @AfterEach
    void afterEach() {
        wireMock.checkForUnmatchedRequests();
    }

    @Test
    void recupererConference() {
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
    void publierConference() {
        // Given
        Conference conference = new Conference(456L, "Vive les tests", "la description", Conference.StatusConference.EN_REDACTION);
        wireMock.stubFor(post("/api/talks")
                .withBasicAuth("login", "password")
                .withRequestBody(equalToJson("""
                        {
                            "nom": "Vive les tests",
                            "description": "la description"
                        }
                        """))
                .willReturn(created()));

        // When
        sut.publierConference(conference);

        // Then
    }

    @Test
    void throwUneExceptionQuandPublierConferenceEnErreur() {
        // Given
        Conference conference = new Conference(456L, "Vive les tests", "la description", Conference.StatusConference.EN_REDACTION);
        wireMock.stubFor(post("/api/talks")
                .withBasicAuth("login", "password")
                .withRequestBody(equalToJson("""
                        {
                            "nom": "Vive les tests",
                            "description": "la description"
                        }
                        """))
                .willReturn(serverError()));

        // When
        ErreurInconnueException result = catchThrowableOfType(ErreurInconnueException.class, () -> sut.publierConference(conference));

        // Then
        assertThat(((HttpServerErrorException.InternalServerError) result.getException()).getStatusText()).isEqualTo("Server Error");
        assertThat(((HttpServerErrorException.InternalServerError) result.getException()).getStatusCode()).isEqualTo(HttpStatusCode.valueOf(500));
    }

    @Test
    @SneakyThrows
    void backUpConference() {
        // Given
        s3Template.createBucket("conference");
        Conference conference = new Conference(456L, "Vive les tests", "la description", Conference.StatusConference.EN_REDACTION);

        // When
        sut.backUpConference(conference);

        // Then
        assertThat(s3Template.objectExists("conference", "456.txt")).isTrue();
        s3Template.deleteObject("conference", "456.txt");
        s3Template.deleteBucket("conference");
    }

    @Test
    void throwUneExceptionQuandBackUpConferenceEnErreur() {
        // Given
        s3Template.createBucket("mauvais-bucket");
        Conference conference = new Conference(456L, "Vive les tests", "la description", Conference.StatusConference.EN_REDACTION);

        // When
        ErreurInconnueException result = catchThrowableOfType(ErreurInconnueException.class, () -> sut.backUpConference(conference));

        // Then
        assertThat(result.getException().getMessage()).isEqualTo("Failed to upload object with a key '456.txt' to bucket 'conference'");
        s3Template.deleteBucket("mauvais-bucket");
    }
}
