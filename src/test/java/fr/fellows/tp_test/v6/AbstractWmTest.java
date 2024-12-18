package fr.fellows.tp_test.v6;

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
                .withRequestBody(equalToJson("""
                        {
                            "nom": "Vive les tests",
                            "desc": "la description"
                        }
                        """))
                .willReturn(ok()));
    }

}
