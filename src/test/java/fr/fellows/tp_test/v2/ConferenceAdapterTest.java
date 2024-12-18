package fr.fellows.tp_test.v2;

import fr.fellows.tp_test.domain.model.Conference;
import fr.fellows.tp_test.infrastructure.adapter.ConferenceAdapter;
import fr.fellows.tp_test.infrastructure.database.ConferenceRepository;
import fr.fellows.tp_test.infrastructure.mapper.ConferenceInfraMapperImpl;
import fr.fellows.tp_test.infrastructure.sessionize.PostTalkRequestSessionize;
import fr.fellows.tp_test.infrastructure.sessionize.SessionizeConfigurationProperties;
import fr.fellows.tp_test.infrastructure.sessionize.SessionizeProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ConferenceAdapter.class, SessionizeProvider.class, ConferenceInfraMapperImpl.class})
class ConferenceAdapterTest {

    @MockBean
    RestTemplate restTemplateMock;

    @MockBean
    SessionizeConfigurationProperties sessionizeConfigurationPropertiesMock;

    @MockBean
    ConferenceRepository conferenceRepositoryMock;

    @Autowired
    ConferenceAdapter sut;

    @Captor
    ArgumentCaptor<String> captorUrl;

    @Captor
    ArgumentCaptor<PostTalkRequestSessionize> captorBody;

    @Test
    void rtete() {
        // Given
        Conference conference = new Conference(456L, "Vive les tests", "la description", Conference.StatusConference.EN_REDACTION);
        when(sessionizeConfigurationPropertiesMock.getBaseUrl()).thenReturn("https://url.com");
        when(restTemplateMock.postForEntity(captorUrl.capture(), captorBody.capture(), any())).thenReturn(ResponseEntity.accepted().build());

        // When
        sut.publierConference(conference);

        // Then
        assertThat(captorUrl.getValue()).isEqualTo("https://url.com/api/talks");
        assertThat(captorBody.getValue()).isEqualTo(new PostTalkRequestSessionize("Vive les tests", "la description"));
    }
}
