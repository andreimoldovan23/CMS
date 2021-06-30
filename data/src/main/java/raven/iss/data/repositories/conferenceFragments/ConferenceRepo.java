package raven.iss.data.repositories.conferenceFragments;

import org.springframework.data.repository.CrudRepository;
import raven.iss.data.model.Conference;

public interface ConferenceRepo extends CrudRepository<Conference, Integer>, ConferenceRepoCustom {
}
