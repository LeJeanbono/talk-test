package fr.fellows.tp_test.v6;

import lombok.SneakyThrows;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AbstractItTest extends AbstractDataTest {

    @Autowired
    MockMvc mvc;

    @SneakyThrows
    protected MvcResult doRequest(MockHttpServletRequestBuilder request) {
        return mvc.perform(request).andReturn();
    }

    protected void assertStatus(MvcResult result, int status) {
        assertThat(result.getResponse().getStatus()).isEqualTo(status);
    }

    @SneakyThrows
    protected void assertBody(MvcResult result, String expectedJson) {
        JSONAssert.assertEquals(
                result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                expectedJson,
                true
        );
    }

}
