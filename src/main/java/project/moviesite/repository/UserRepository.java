package project.moviesite.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.moviesite.model.User;

public interface UserRepository extends JpaRepository<User, String> {
    User findBySub(String sub);

    @Query("SELECT u.username, COUNT(r) " + "FROM User u "
            + "JOIN u.ratings r "
            + "JOIN r.movie m "
            + "JOIN m.genres g "
            + "WHERE g.genreName = :genreName "
            + "GROUP BY u.username "
            + "ORDER BY COUNT(r) DESC")
    List<Object[]> findTopUsersByGenreRatings(@Param("genreName") String genreName);
}
