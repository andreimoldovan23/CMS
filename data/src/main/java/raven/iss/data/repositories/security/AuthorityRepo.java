package raven.iss.data.repositories.security;

import org.springframework.data.jpa.repository.JpaRepository;
import raven.iss.data.model.security.Authority;

public interface AuthorityRepo extends JpaRepository<Authority, Integer> {
    Authority findByPermission(String permission);
}
