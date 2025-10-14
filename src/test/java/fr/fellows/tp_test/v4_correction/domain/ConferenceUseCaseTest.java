package fr.fellows.tp_test.v4_correction.domain;

import fr.fellows.tp_test.domain.exception.RessourceNonTrouveeException;
import fr.fellows.tp_test.domain.model.Conference;
import fr.fellows.tp_test.domain.port.out.ConferencePortOut;
import fr.fellows.tp_test.domain.usecase.ConferenceUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConferenceUseCaseTest {

    @Mock
    ConferencePortOut conferencePortMock;

    @InjectMocks
    ConferenceUseCase sut;

    @Captor
    ArgumentCaptor<Conference> captorConference;

    @Captor
    ArgumentCaptor<Long> captorId;

    @Test
    void publierConference() {
        // Given
        Conference conference = new Conference(789L, "Vive les tests", "la description", Conference.StatusConference.EN_REDACTION);
        when(conferencePortMock.recupererConference(captorId.capture())).thenReturn(conference);
        doNothing().when(conferencePortMock).publierConference(captorConference.capture());

        // When
        Conference result = sut.publierConference(789L);

        // Then
        assertThat(result).usingRecursiveComparison().isEqualTo(new Conference(789L, "Vive les tests", "la description", Conference.StatusConference.PUBLIEE));
        assertThat(captorId.getValue()).isEqualTo(789L);
        assertThat(captorConference.getValue()).usingRecursiveComparison().isEqualTo(new Conference(789L, "Vive les tests", "la description", Conference.StatusConference.PUBLIEE));
    }

    @Test
    void throwUneExceptionEchouerPublicationQuandConferenceInconnue() {
        // Given
        when(conferencePortMock.recupererConference(any())).thenThrow(new RessourceNonTrouveeException(789L));

        // When
        RessourceNonTrouveeException exception = assertThrows(RessourceNonTrouveeException.class, () -> sut.publierConference(789L));

        // Then
        assertThat(exception).usingRecursiveComparison().isEqualTo(new RessourceNonTrouveeException(789L));

    }
}
