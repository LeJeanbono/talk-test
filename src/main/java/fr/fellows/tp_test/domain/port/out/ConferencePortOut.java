package fr.fellows.tp_test.domain.port.out;

import fr.fellows.tp_test.domain.exception.RessourceNonTrouveeException;
import fr.fellows.tp_test.domain.model.Conference;

public interface ConferencePortOut {

    Conference recupererConference(Long id) throws RessourceNonTrouveeException;

    void publierConference(Conference conference);

}
