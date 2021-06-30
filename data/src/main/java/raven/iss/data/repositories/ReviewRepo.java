package raven.iss.data.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import raven.iss.data.model.Review;

import java.util.List;

public interface ReviewRepo  extends CrudRepository<Review, Integer> {
    @Query("select r from Review r where r.paper.id = ?1")
    List<Review> findAllByPaperId(Integer paperId);
}
