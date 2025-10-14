package fr.fellows.tp_test.v4_correction.application;

import fr.fellows.tp_test.domain.exception.ErreurInconnueException;
import fr.fellows.tp_test.domain.exception.RessourceNonTrouveeException;
import fr.fellows.tp_test.domain.model.Conference;
import fr.fellows.tp_test.domain.port.in.ConferencePortIn;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ConferenceControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ConferencePortIn conferencePortInMock;

    @Captor
    ArgumentCaptor<Long> captorId;

    @Test
    @SneakyThrows
    void publierConference() {
        // Given
        Conference conference = new Conference(123L, "Vive les tests", "la description", Conference.StatusConference.PUBLIEE);
        when(conferencePortInMock.publierConference(captorId.capture())).thenReturn(conference);

        // When
        MvcResult result = mvc.perform(post("/api/v1/conferences/123/publish")).andReturn();

        // Then
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        JSONAssert.assertEquals(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                """
                        {
                            "id":123,
                            "nom":"Vive les tests",
                            "description":"la description",
                            "status":"PUBLIEE"
                        }
                        """, true
        );
        assertThat(captorId.getValue()).isEqualTo(123L);
    }

    @Test
    void echoueQuandConferenceInconnue() throws Exception {
        // Given
        when(conferencePortInMock.publierConference(captorId.capture())).thenThrow(new RessourceNonTrouveeException(123L));

        // When
        MvcResult result = mvc.perform(post("/api/v1/conferences/123/publish")).andReturn();

        // Then
        assertThat(result.getResponse().getStatus()).isEqualTo(404);
        JSONAssert.assertEquals(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                """
                        {
                            "error": "Ressource 123 introuvable"
                        }
                        """, true
        );
        assertThat(captorId.getValue()).isEqualTo(123L);
    }

    @Test
    void echoueQuandErreurInconnue() throws Exception {
        // Given
        when(conferencePortInMock.publierConference(captorId.capture())).thenThrow(new ErreurInconnueException(new Exception()));

        // When
        MvcResult result = mvc.perform(post("/api/v1/conferences/123/publish")).andReturn();

        // Then
        assertThat(result.getResponse().getStatus()).isEqualTo(500);
        JSONAssert.assertEquals(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                """
                        {
                            "error": "Erreur inconnue"
                        }
                        """, true
        );
        assertThat(captorId.getValue()).isEqualTo(123L);
    }

}
