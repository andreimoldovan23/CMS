package raven.iss.data.repositories.sessionFragments;

import raven.iss.data.model.Session;

public interface SessionRepoCustom {
    Session findByIdSpeakersWatchers(Integer id);
    Session findByIdSpeakers(Integer id);
    Session findByIdWatchers(Integer id);
}
