package fr.fellows.tp_test.application.conference;

import fr.fellows.tp_test.domain.model.Conference;
import org.mapstruct.Mapper;

@Mapper
public interface ConferenceApplicationMapper {

    ConferenceDto conferenceToConferenceDto(Conference conference);

}
