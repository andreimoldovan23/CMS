package raven.iss.data.repositories.conferenceFragments;

import raven.iss.data.model.Conference;

public interface ConferenceRepoCustom {
    Conference findByIdWithChairs(Integer id);
    Conference findByIdWithPCMembers(Integer id);
    Conference findByIdWithPhases(Integer id);
    Conference findByIdWithRooms(Integer id);
    Conference findByIdWithAuthors(Integer id);
    Conference findByIdWithListeners(Integer id);
    Conference findByIdWithSCR(Integer id);
    Conference findByIdWithSessions(Integer id);
    Conference findByIdWithMembersPapers(Integer id);
}
