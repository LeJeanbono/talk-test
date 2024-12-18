package fr.fellows.tp_test.v1;

import fr.fellows.tp_test.application.conference.ConferenceApplicationMapper;
import fr.fellows.tp_test.application.conference.ConferenceApplicationMapperImpl;
import fr.fellows.tp_test.application.conference.ConferenceController;
import fr.fellows.tp_test.application.conference.ConferenceDto;
import fr.fellows.tp_test.domain.model.Conference;
import fr.fellows.tp_test.domain.port.in.ConferencePortIn;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConferenceControllerTest {

    @Spy
    private ConferenceApplicationMapper mapper = new ConferenceApplicationMapperImpl();

    @Mock
    private ConferencePortIn conferencePortInMock;

    @InjectMocks
    private ConferenceController sut;

    @Captor
    ArgumentCaptor<Long> captorId;

    @Test
    void erer() {
        // Given
        Conference conference = new Conference(123L, "Vive les tests", "la description", Conference.StatusConference.PUBLIEE);
        when(conferencePortInMock.publierConference(captorId.capture())).thenReturn(conference);

        // When
        ConferenceDto result = sut.publierConference(123L);

        // Then
        assertThat(result).usingRecursiveAssertion().isEqualTo(new ConferenceDto(123L, "Vive les tests", "la description", "PUBLIEE"));
        assertThat(captorId.getValue()).isEqualTo(123L);
    }

}
