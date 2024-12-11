package project.moviesite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.moviesite.model.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Rating findByUserSubAndMovieId(String userSub, Long movieId);
}
