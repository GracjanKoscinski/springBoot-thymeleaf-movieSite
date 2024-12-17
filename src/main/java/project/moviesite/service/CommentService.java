package project.moviesite.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import project.moviesite.model.Comment;
import project.moviesite.model.Movie;
import project.moviesite.model.User;
import project.moviesite.repository.CommentRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void addComment(String text, User user, Movie movie) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setUser(user);
        comment.setMovie(movie);
        commentRepository.save(comment);
    }

    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new EntityNotFoundException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }

    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    public long countCommentsByUser(User user) {
        return commentRepository.countByUser(user);
    }
}
