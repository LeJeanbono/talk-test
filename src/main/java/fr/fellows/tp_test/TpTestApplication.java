package fr.fellows.tp_test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class TpTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TpTestApplication.class, args);
    }

}
