package fr.fellows.tp_test;

import fr.fellows.tp_test.domain.port.out.ConferencePortOut;
import fr.fellows.tp_test.domain.usecase.ConferenceUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfiguration {

    @Bean
    public ConferenceUseCase creerConference(ConferencePortOut conferencePortOut) {
        return new ConferenceUseCase(conferencePortOut);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
