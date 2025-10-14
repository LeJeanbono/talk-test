package fr.fellows.tp_test.v3_correction.infrastructure;

import fr.fellows.tp_test.domain.exception.ErreurInconnueException;
import fr.fellows.tp_test.domain.model.Conference;
import fr.fellows.tp_test.infrastructure.adapter.ConferenceAdapter;
import fr.fellows.tp_test.infrastructure.database.ConferenceEntity;
import fr.fellows.tp_test.infrastructure.database.ConferenceRepository;
import fr.fellows.tp_test.infrastructure.mapper.ConferenceInfraMapperImpl;
import fr.fellows.tp_test.infrastructure.s3.S3ConfigurationProperties;
import fr.fellows.tp_test.infrastructure.s3.S3Provider;
import fr.fellows.tp_test.infrastructure.sessionize.PostTalkRequestSessionizeDto;
import fr.fellows.tp_test.infrastructure.sessionize.SessionizeConfigurationProperties;
import fr.fellows.tp_test.infrastructure.sessionize.SessionizeProvider;
import io.awspring.cloud.s3.S3Exception;
import io.awspring.cloud.s3.S3Template;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ConferenceAdapter.class, SessionizeProvider.class, ConferenceInfraMapperImpl.class, S3Provider.class})
class ConferenceAdapterTest {

    @MockitoBean
    RestTemplate restTemplateMock;

    @MockitoBean
    SessionizeConfigurationProperties sessionizeConfigurationPropertiesMock;

    @MockitoBean
    ConferenceRepository conferenceRepositoryMock;

    @MockitoBean
    S3ConfigurationProperties s3ConfigurationPropertiesMock;

    @MockitoBean
    S3Template s3TemplateMock;

    @Autowired
    ConferenceAdapter sut;

    @Captor
    ArgumentCaptor<String> captorUrl;

    @Captor
    ArgumentCaptor<PostTalkRequestSessionizeDto> captorBody;

    @Captor
    ArgumentCaptor<Long> captorId;

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
        entity.setId(456L);
        entity.setNom("Vive les tests");
        entity.setDescription("la description");
        when(conferenceRepositoryMock.findById(captorId.capture())).thenReturn(Optional.of(entity));

        // When
        Conference result = sut.recupererConference(456L);

        // Then
        assertThat(result).usingRecursiveComparison().isEqualTo(new Conference(456L, "Vive les tests", "la description", null));
        assertThat(captorId.getValue()).isEqualTo(456L);
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
