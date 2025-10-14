package fr.fellows.tp_test.v1.infrastruture;

import fr.fellows.tp_test.domain.exception.ErreurInconnueException;
import fr.fellows.tp_test.domain.model.Conference;
import fr.fellows.tp_test.infrastructure.adapter.ConferenceAdapter;
import fr.fellows.tp_test.infrastructure.database.ConferenceEntity;
import fr.fellows.tp_test.infrastructure.database.ConferenceRepository;
import fr.fellows.tp_test.infrastructure.mapper.ConferenceInfraMapper;
import fr.fellows.tp_test.infrastructure.mapper.ConferenceInfraMapperImpl;
import fr.fellows.tp_test.infrastructure.s3.S3Provider;
import fr.fellows.tp_test.infrastructure.sessionize.SessionizeProvider;
import io.awspring.cloud.s3.S3Exception;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConferenceAdapterTest {

    @Spy
    ConferenceInfraMapper mapper = new ConferenceInfraMapperImpl();

    @Mock
    SessionizeProvider sessionizeProviderMock;

    @Mock
    ConferenceRepository conferenceRepositoryMock;

    @Mock
    S3Provider s3ProviderMock;

    @InjectMocks
    ConferenceAdapter sut;

    @Captor
    ArgumentCaptor<Long> captorId;

    @Captor
    ArgumentCaptor<Conference> captorConference;

    @Captor
    ArgumentCaptor<String> captorFileName;

    @Captor
    ArgumentCaptor<String> captorFileContent;

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
        doNothing().when(sessionizeProviderMock).publierConference(captorConference.capture());

        // When
        sut.publierConference(conference);

        // Then
        assertThat(captorConference.getValue()).usingRecursiveComparison().isEqualTo(new Conference(456L, "Vive les tests", "la description", Conference.StatusConference.EN_REDACTION));
    }

    @Test
    void throwUneExceptionQuandPublierConferenceEnErreur() {
        // Given
        Exception exception = new HttpClientErrorException(HttpStatusCode.valueOf(400));
        Conference conference = new Conference(456L, "Vive les tests", "la description", Conference.StatusConference.EN_REDACTION);
        doThrow(exception).when(sessionizeProviderMock).publierConference(captorConference.capture());

        // When
        ErreurInconnueException result = catchThrowableOfType(ErreurInconnueException.class, () -> sut.publierConference(conference));

        // Then
        assertThat(result.getException()).isEqualTo(exception);
    }

    @Test
    void backUpConference() {
        // Given
        Conference conference = new Conference(456L, "Vive les tests", "la description", Conference.StatusConference.EN_REDACTION);
        doNothing().when(s3ProviderMock).upload(captorFileName.capture(), captorFileContent.capture());

        // When
        sut.backUpConference(conference);

        // Then
        assertThat(captorFileName.getValue()).isEqualTo("456.txt");
        assertThat(captorFileContent.getValue()).isEqualTo("Vive les tests\r\nla description");
    }

    @Test
    void throwUneExceptionQuandBackUpConferenceEnErreur() {
        // Given
        Exception exception = new S3Exception("erreur S3", null);
        Conference conference = new Conference(456L, "Vive les tests", "la description", Conference.StatusConference.EN_REDACTION);
        doThrow(exception).when(s3ProviderMock).upload(captorFileName.capture(), captorFileContent.capture());

        // When
        ErreurInconnueException result = catchThrowableOfType(ErreurInconnueException.class, () -> sut.backUpConference(conference));

        // Then
        assertThat(result.getException()).isEqualTo(exception);
    }
}
