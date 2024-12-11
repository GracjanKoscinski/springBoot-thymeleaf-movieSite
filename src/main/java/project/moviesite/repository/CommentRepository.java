package project.moviesite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.moviesite.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByMovieId(Long movieId);
}