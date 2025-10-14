package fr.fellows.tp_test.v1.infrastruture;

import fr.fellows.tp_test.infrastructure.s3.S3ConfigurationProperties;
import fr.fellows.tp_test.infrastructure.s3.S3Provider;
import io.awspring.cloud.s3.S3Template;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ProviderTest {

    @Mock
    S3ConfigurationProperties s3ConfigurationPropertiesMock;

    @Mock
    S3Template s3TemplateMock;

    @InjectMocks
    S3Provider sut;

    @Captor
    ArgumentCaptor<String> captorBucket;

    @Captor
    ArgumentCaptor<String> captorKey;

    @Captor
    ArgumentCaptor<InputStream> captorStream;

    @Test
    @SneakyThrows
    void uploaderUnFichier() {
        // Given
        when(s3ConfigurationPropertiesMock.getBucketConference()).thenReturn("bucket-test");
        when(s3TemplateMock.upload(captorBucket.capture(), captorKey.capture(), captorStream.capture())).thenReturn(null);

        // When
        sut.upload("fichier.txt", "contenu du fichier");

        // Then
        assertThat(captorBucket.getValue()).isEqualTo("bucket-test");
        assertThat(captorKey.getValue()).isEqualTo("fichier.txt");
        assertThat(captorStream.getValue().readAllBytes()).isEqualTo("contenu du fichier".getBytes());
    }
}
