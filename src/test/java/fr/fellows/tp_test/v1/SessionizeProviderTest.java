package fr.fellows.tp_test.v1;

import fr.fellows.tp_test.domain.model.Conference;
import fr.fellows.tp_test.infrastructure.sessionize.PostTalkRequestSessionize;
import fr.fellows.tp_test.infrastructure.sessionize.SessionizeConfigurationProperties;
import fr.fellows.tp_test.infrastructure.sessionize.SessionizeProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionizeProviderTest {

    @Mock
    SessionizeConfigurationProperties sessionizeConfigurationPropertiesMock;

    @Mock
    RestTemplate restTemplateMock;

    @InjectMocks
    SessionizeProvider sut;

    @Captor
    ArgumentCaptor<String> captorUrl;

    @Captor
    ArgumentCaptor<PostTalkRequestSessionize> captorBody;

    @Test
    void hfhgdgh() {
        // Given
        Conference conference = new Conference(123L, "Vive les tests", "la description", Conference.StatusConference.EN_REDACTION);
        when(sessionizeConfigurationPropertiesMock.getBaseUrl()).thenReturn("https://url.com");
        when(restTemplateMock.postForEntity(captorUrl.capture(), captorBody.capture(), any())).thenReturn(ResponseEntity.accepted().build());

        // When
        sut.publierConference(conference);

        // Then
        assertThat(captorUrl.getValue()).isEqualTo("https://url.com/api/talks");
        assertThat(captorBody.getValue()).isEqualTo(new PostTalkRequestSessionize("Vive les tests", "la description"));
    }

}
