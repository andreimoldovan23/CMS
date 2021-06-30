package raven.iss.data.repositories.listenerFragments;

import raven.iss.data.model.Listener;

public interface ListenerRepoCustom {
    Listener findByUsernameAndConfIdWithSessions(String username, Integer id);
}
