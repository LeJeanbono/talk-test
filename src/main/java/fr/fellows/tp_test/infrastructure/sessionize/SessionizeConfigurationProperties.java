package fr.fellows.tp_test.infrastructure.sessionize;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Component
@ConfigurationProperties("sessionize")
public class SessionizeConfigurationProperties {

    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }
    
}
