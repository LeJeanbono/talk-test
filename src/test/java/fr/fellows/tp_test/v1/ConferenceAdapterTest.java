package fr.fellows.tp_test.v1;

import fr.fellows.tp_test.domain.model.Conference;
import fr.fellows.tp_test.infrastructure.adapter.ConferenceAdapter;
import fr.fellows.tp_test.infrastructure.database.ConferenceEntity;
import fr.fellows.tp_test.infrastructure.database.ConferenceRepository;
import fr.fellows.tp_test.infrastructure.mapper.ConferenceInfraMapper;
import fr.fellows.tp_test.infrastructure.mapper.ConferenceInfraMapperImpl;
import fr.fellows.tp_test.infrastructure.sessionize.SessionizeProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConferenceAdapterTest {

    @Spy
    ConferenceInfraMapper mapper = new ConferenceInfraMapperImpl();

    @Mock
    SessionizeProvider sessionizeProviderMock;

    @Mock
    ConferenceRepository conferenceRepositoryMock;

    @InjectMocks
    ConferenceAdapter sut;

    @Captor
    ArgumentCaptor<Long> captorId;

    @Captor
    ArgumentCaptor<Conference> captorConference;

    @Test
    void etete() {
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
    void hsshs() {
        // Given
        Conference conference = new Conference(456L, "Vive les tests", "la description", Conference.StatusConference.EN_REDACTION);
        doNothing().when(sessionizeProviderMock).publierConference(captorConference.capture());

        // When
        sut.publierConference(conference);

        // Then
        assertThat(captorConference.getValue()).usingRecursiveComparison().isEqualTo(new Conference(456L, "Vive les tests", "la description", Conference.StatusConference.EN_REDACTION));
    }
}
