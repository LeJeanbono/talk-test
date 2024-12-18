package fr.fellows.tp_test.domain.usecase;

import fr.fellows.tp_test.domain.exception.RessourceNonTrouveeException;
import fr.fellows.tp_test.domain.model.Conference;
import fr.fellows.tp_test.domain.port.in.ConferencePortIn;
import fr.fellows.tp_test.domain.port.out.ConferencePortOut;

public class ConferenceUseCase implements ConferencePortIn {

    private final ConferencePortOut conferencePort;

    public ConferenceUseCase(ConferencePortOut conferencePort) {
        this.conferencePort = conferencePort;
    }

    @Override
    public Conference publierConference(Long id) {
        try {
            Conference conference = conferencePort.recupererConference(id);
            conferencePort.publierConference(conference);
            conference.publish();
            return conference;
        } catch (RessourceNonTrouveeException ex) {
            throw new RessourceNonTrouveeException(id);
        }
    }
}
