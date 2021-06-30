package raven.iss.data.repositories;

import org.springframework.data.repository.CrudRepository;
import raven.iss.data.model.User;

public interface UserRepo extends CrudRepository<User, Integer> {
    User findByUsername(String username);
}
