package project.moviesite.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import project.moviesite.model.Comment;
import project.moviesite.model.User;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByMovieId(Long movieId);

    long countByUser(User user);
}
