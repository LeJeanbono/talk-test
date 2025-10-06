package fr.fellows.tp_test.infrastructure.adapter;

import fr.fellows.tp_test.domain.exception.ErreurInconnueException;
import fr.fellows.tp_test.domain.exception.RessourceNonTrouveeException;
import fr.fellows.tp_test.domain.model.Conference;
import fr.fellows.tp_test.domain.port.out.ConferencePortOut;
import fr.fellows.tp_test.infrastructure.database.ConferenceEntity;
import fr.fellows.tp_test.infrastructure.database.ConferenceRepository;
import fr.fellows.tp_test.infrastructure.mapper.ConferenceInfraMapper;
import fr.fellows.tp_test.infrastructure.s3.S3Provider;
import fr.fellows.tp_test.infrastructure.sessionize.SessionizeProvider;
import io.awspring.cloud.s3.S3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConferenceAdapter implements ConferencePortOut {

    private final ConferenceRepository conferenceRepository;
    private final SessionizeProvider sessionizeProvider;
    private final S3Provider s3Provider;
    private final ConferenceInfraMapper conferenceInfraMapper;

    @Override
    public Conference recupererConference(Long id) {
        ConferenceEntity entity = conferenceRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException(id));
        return conferenceInfraMapper.conferenceEntityToConference(entity);
    }

    @Override
    public void publierConference(Conference conference) {
        try {
            sessionizeProvider.publierConference(conference);
        } catch (RestClientResponseException e) {
            log.error("Erreur publication conférence", e);
            throw new ErreurInconnueException(e);
        }
    }

    @Override
    public void backUpConference(Conference conference) {
        try {
            s3Provider.upload(conference.getId().toString() + ".txt", conference.getNom() + "\r\n" + conference.getDescription());
        } catch (S3Exception e) {
            log.error("Erreur backup conférence", e);
            throw new ErreurInconnueException(e);
        }
    }

}
