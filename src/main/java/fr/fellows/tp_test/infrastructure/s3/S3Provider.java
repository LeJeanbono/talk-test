package fr.fellows.tp_test.infrastructure.s3;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class S3Provider {

    private final S3ConfigurationProperties s3ConfigurationProperties;
    private final S3Template s3Template;

    public void upload(String fileName, String fileContent) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));
        this.s3Template.upload(s3ConfigurationProperties.getBucketConference(), fileName, inputStream);
    }

}
