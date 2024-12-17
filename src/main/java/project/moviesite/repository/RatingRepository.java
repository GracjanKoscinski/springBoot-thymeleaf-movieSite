package project.moviesite.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.moviesite.model.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Rating findByUserSubAndMovieId(String userSub, Long movieId);

    @Query("SELECT g.genreName, AVG(r.starsRating) as avgRating " + "FROM Movie m "
            + "JOIN m.genres g "
            + "JOIN m.ratings r "
            + "GROUP BY g.genreName")
    List<Object[]> findAverageRatingsByGenre();
}
