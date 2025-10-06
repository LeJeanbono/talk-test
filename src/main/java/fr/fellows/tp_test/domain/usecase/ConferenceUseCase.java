package fr.fellows.tp_test.domain.usecase;

import fr.fellows.tp_test.UseCase;
import fr.fellows.tp_test.domain.model.Conference;
import fr.fellows.tp_test.domain.port.in.ConferencePortIn;
import fr.fellows.tp_test.domain.port.out.ConferencePortOut;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class ConferenceUseCase implements ConferencePortIn {

    private final ConferencePortOut conferencePort;

    @Override
    public Conference publierConference(Long id) {
        Conference conference = conferencePort.recupererConference(id);
        conferencePort.backUpConference(conference);
        conferencePort.publierConference(conference);
        conference.publish();
        return conference;
    }
}
