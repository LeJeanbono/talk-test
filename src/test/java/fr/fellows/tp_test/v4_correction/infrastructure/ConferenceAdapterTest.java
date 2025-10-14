package fr.fellows.tp_test.v4_correction.infrastructure;

import fr.fellows.tp_test.domain.exception.ErreurInconnueException;
import fr.fellows.tp_test.domain.model.Conference;
import fr.fellows.tp_test.infrastructure.adapter.ConferenceAdapter;
import fr.fellows.tp_test.infrastructure.database.ConferenceEntity;
import fr.fellows.tp_test.infrastructure.database.ConferenceRepository;
import fr.fellows.tp_test.infrastructure.s3.S3ConfigurationProperties;
import fr.fellows.tp_test.infrastructure.sessionize.PostTalkRequestSessionizeDto;
import fr.fellows.tp_test.infrastructure.sessionize.SessionizeConfigurationProperties;
import io.awspring.cloud.s3.S3Exception;
import io.awspring.cloud.s3.S3Template;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class ConferenceAdapterTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:latest");

    @MockitoBean
    RestTemplate restTemplateMock;

    @MockitoBean
    SessionizeConfigurationProperties sessionizeConfigurationPropertiesMock;

    @MockitoBean
    S3ConfigurationProperties s3ConfigurationPropertiesMock;

    @MockitoBean
    S3Template s3TemplateMock;

    @Autowired
    ConferenceAdapter sut;

    @Autowired
    ConferenceRepository conferenceRepository;

    @Captor
    ArgumentCaptor<String> captorUrl;

    @Captor
    ArgumentCaptor<PostTalkRequestSessionizeDto> captorBody;

    @Captor
    ArgumentCaptor<String> captorBucket;

    @Captor
    ArgumentCaptor<String> captorKey;

    @Captor
    ArgumentCaptor<InputStream> captorStream;


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
        when(sessionizeConfigurationPropertiesMock.getBaseUrl()).thenReturn("https://url.com");
        when(restTemplateMock.postForEntity(captorUrl.capture(), captorBody.capture(), any())).thenReturn(ResponseEntity.accepted().build());

        // When
        sut.publierConference(conference);

        // Then
        assertThat(captorUrl.getValue()).isEqualTo("https://url.com/api/talks");
        assertThat(captorBody.getValue()).isEqualTo(new PostTalkRequestSessionizeDto("Vive les tests", "la description"));
    }

    @Test
    void throwUneExceptionQuandPublierConferenceEnErreur() {
        // Given
        Exception exception = new HttpClientErrorException(HttpStatusCode.valueOf(400));
        Conference conference = new Conference(456L, "Vive les tests", "la description", Conference.StatusConference.EN_REDACTION);
        doThrow(exception).when(restTemplateMock).postForEntity(captorUrl.capture(), captorBody.capture(), any());

        // When
        ErreurInconnueException result = catchThrowableOfType(ErreurInconnueException.class, () -> sut.publierConference(conference));

        // Then
        assertThat(result.getException()).isEqualTo(exception);
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
