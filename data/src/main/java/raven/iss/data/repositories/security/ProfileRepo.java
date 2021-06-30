package raven.iss.data.repositories.security;

import org.springframework.data.jpa.repository.JpaRepository;
import raven.iss.data.model.security.Profile;

import java.util.Optional;

public interface ProfileRepo extends JpaRepository<Profile, Integer> {
    Optional<Profile> findByUsername(String username);
}
