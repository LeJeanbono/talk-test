package fr.fellows.tp_test.application.conference;

import fr.fellows.tp_test.domain.model.Conference;
import fr.fellows.tp_test.domain.port.in.ConferencePortIn;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/")
@RestController
@RequiredArgsConstructor
public class ConferenceController {

    private final ConferencePortIn conferencePortIn;
    private final ConferenceApplicationMapper conferenceMapper;

    @PostMapping("conferences/{id}/publish")
    public ConferenceDto publierConference(@PathVariable Long id) {
        Conference conference = conferencePortIn.publierConference(id);
        return conferenceMapper.conferenceToConferenceDto(conference);
    }

}
