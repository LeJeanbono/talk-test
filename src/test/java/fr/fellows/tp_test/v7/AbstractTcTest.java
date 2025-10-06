package fr.fellows.tp_test.v7;

import io.awspring.cloud.s3.S3Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class AbstractTcTest extends AbstractWmTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:latest");

    @ServiceConnection
    static LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.4"))
            .withServices(LocalStackContainer.Service.S3);

    @Autowired
    protected S3Template s3Template;

    protected void creerBucketConference() {
        s3Template.createBucket("conference");
    }

    protected void supprimerBucket(String bucketName) {
        s3Template.listObjects("conference", "")
                .forEach(s3Object -> s3Template.deleteObject("conference", s3Object.getFilename()));
        s3Template.deleteBucket(bucketName);
    }

}
