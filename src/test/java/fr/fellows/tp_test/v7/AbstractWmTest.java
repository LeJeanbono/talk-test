package fr.fellows.tp_test.v7;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@EnableWireMock(@ConfigureWireMock(
        baseUrlProperties = {"sessionize.base-url"}
))
public class AbstractWmTest {

    @InjectWireMock
    WireMockServer wireMock;

    protected void stubPublish() {
        wireMock.stubFor(post("/api/talks")
                .withBasicAuth("login", "password")
                .withRequestBody(equalToJson("""
                        {
                            "nom": "Vive les tests",
                            "description": "la description"
                        }
                        """))
                .willReturn(ok()));
    }

    protected void stubPublishEnErreur() {
        wireMock.stubFor(post("/api/talks")
                .withBasicAuth("login", "password")
                .withRequestBody(equalToJson("""
                        {
                            "nom": "Vive les tests",
                            "description": "la description"
                        }
                        """))
                .willReturn(serverError()));
    }

}
