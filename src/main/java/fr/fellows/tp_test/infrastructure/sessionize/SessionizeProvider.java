package fr.fellows.tp_test.infrastructure.sessionize;

import fr.fellows.tp_test.domain.model.Conference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class SessionizeProvider {

    private final RestTemplate restTemplate;
    private final SessionizeConfigurationProperties sessionizeConfigurationProperties;

    public void publierConference(Conference conference) {
        PostTalkRequestSessionize request = new PostTalkRequestSessionize(conference.getNom(), conference.getDescription());
        restTemplate.postForEntity(sessionizeConfigurationProperties.getBaseUrl() + "/api/talks", request, Void.class);
    }
}
