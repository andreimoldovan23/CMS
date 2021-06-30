package raven.iss.data.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import raven.iss.data.model.Room;

import java.util.Set;

public interface RoomRepo extends CrudRepository<Room, Integer> {

    @Query("select r from Room r " +
            "inner join Conference c " +
            "on r.conf = c where c.id = ?1")
    Set<Room> findRoomsByConfId(Integer id);

}
