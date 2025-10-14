package fr.fellows.tp_test.v7_correction;

import fr.fellows.tp_test.infrastructure.database.ConferenceEntity;
import fr.fellows.tp_test.infrastructure.database.ConferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractDataTest extends AbstractTcTest {

    @Autowired
    ConferenceRepository conferenceRepository;

    protected ConferenceEntity addConference() {
        ConferenceEntity entity = new ConferenceEntity();
        entity.setNom("Vive les tests");
        entity.setDescription("la description");
        return conferenceRepository.save(entity);
    }

}
