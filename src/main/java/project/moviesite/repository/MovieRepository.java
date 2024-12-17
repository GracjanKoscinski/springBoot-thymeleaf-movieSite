package project.moviesite.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.moviesite.model.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query("SELECT DISTINCT m FROM Movie m " + "JOIN m.ratings r "
            + "GROUP BY m "
            + "HAVING AVG(r.starsRating) > (SELECT AVG(r2.starsRating) FROM Rating r2)")
    List<Movie> findMoviesWithAboveAverageRating();
}
