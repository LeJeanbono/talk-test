package fr.fellows.tp_test.infrastructure.mapper;

import fr.fellows.tp_test.domain.model.Conference;
import fr.fellows.tp_test.infrastructure.database.ConferenceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ConferenceInfraMapper {

    @Mapping(target = "status", ignore = true)
    Conference conferenceEntityToConference(ConferenceEntity conferenceEntity);

}
