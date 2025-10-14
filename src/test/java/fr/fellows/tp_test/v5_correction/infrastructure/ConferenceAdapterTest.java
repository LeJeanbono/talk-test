package fr.fellows.tp_test.v5_correction.infrastructure;

import com.github.tomakehurst.wiremock.WireMockServer;
import fr.fellows.tp_test.domain.exception.ErreurInconnueException;
import fr.fellows.tp_test.domain.model.Conference;
import fr.fellows.tp_test.infrastructure.adapter.ConferenceAdapter;
import fr.fellows.tp_test.infrastructure.database.ConferenceEntity;
import fr.fellows.tp_test.infrastructure.database.ConferenceRepository;
import fr.fellows.tp_test.infrastructure.s3.S3ConfigurationProperties;
import io.awspring.cloud.s3.S3Exception;
import io.awspring.cloud.s3.S3Template;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.HttpServerErrorException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.io.InputStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

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

    @InjectWireMock
    WireMockServer wireMock;

    @MockitoBean
    S3ConfigurationProperties s3ConfigurationPropertiesMock;

    @MockitoBean
    S3Template s3TemplateMock;

    @Autowired
    ConferenceAdapter sut;

    @Autowired
    ConferenceRepository conferenceRepository;

    @Captor
    ArgumentCaptor<String> captorBucket;

    @Captor
    ArgumentCaptor<String> captorKey;

    @Captor
    ArgumentCaptor<InputStream> captorStream;

    @AfterEach
    void afterEach(){
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
        Conference conference = new Conference(456L, "Vive les tests", "la description", Conference.StatusConference.EN_REDACTION);
        when(s3ConfigurationPropertiesMock.getBucketConference()).thenReturn("bucket-test");
        when(s3TemplateMock.upload(captorBucket.capture(), captorKey.capture(), captorStream.capture())).thenReturn(null);

        // When
        sut.backUpConference(conference);

        // Then
        assertThat(captorBucket.getValue()).isEqualTo("bucket-test");
        assertThat(captorKey.getValue()).isEqualTo("456.txt");
        assertThat(captorStream.getValue().readAllBytes()).isEqualTo("Vive les tests\r\nla description".getBytes());
    }

    @Test
    void throwUneExceptionQuandBackUpConferenceEnErreur() {
        // Given
        Exception exception = new S3Exception("erreur S3", null);
        Conference conference = new Conference(456L, "Vive les tests", "la description", Conference.StatusConference.EN_REDACTION);
        when(s3ConfigurationPropertiesMock.getBucketConference()).thenReturn("bucket-test");
        doThrow(exception).when(s3TemplateMock).upload(any(), any(), any());

        // When
        ErreurInconnueException result = catchThrowableOfType(ErreurInconnueException.class, () -> sut.backUpConference(conference));

        // Then
        assertThat(result.getException()).isEqualTo(exception);
    }
}
