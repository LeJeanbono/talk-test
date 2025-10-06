package fr.fellows.tp_test.infrastructure.s3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties("s3")
@RequiredArgsConstructor
public class S3ConfigurationProperties {

    private String bucketConference;

}
