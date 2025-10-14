package fr.fellows.tp_test.v1.infrastruture;

import fr.fellows.tp_test.infrastructure.sessionize.SessionizeConfigurationProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SessionizeConfigurationPropertiesTest {

    @Test
    void testRestTemplate() {
        // Given
        SessionizeConfigurationProperties config = new SessionizeConfigurationProperties();

        // When
        RestTemplate result = config.restTemplate();

        // Then
        assertThat(result).isNotNull();
    }

}
