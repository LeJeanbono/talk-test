package fr.fellows.tp_test.infrastructure.adapter;

import fr.fellows.tp_test.domain.exception.RessourceNonTrouveeException;
import fr.fellows.tp_test.domain.model.Conference;
import fr.fellows.tp_test.domain.port.out.ConferencePortOut;
import fr.fellows.tp_test.infrastructure.database.ConferenceEntity;
import fr.fellows.tp_test.infrastructure.database.ConferenceRepository;
import fr.fellows.tp_test.infrastructure.mapper.ConferenceInfraMapper;
import fr.fellows.tp_test.infrastructure.sessionize.SessionizeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConferenceAdapter implements ConferencePortOut {

    private final ConferenceRepository conferenceRepository;
    private final SessionizeProvider sessionizeProvider;
    private final ConferenceInfraMapper conferenceInfraMapper;

    @Override
    public Conference recupererConference(Long id) {
        ConferenceEntity entity = conferenceRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException(id));
        return conferenceInfraMapper.conferenceEntityToConference(entity);
    }

    public void publierConference(Conference conference) {
        sessionizeProvider.publierConference(conference);
    }

}
