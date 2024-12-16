package project.moviesite.controller.rest;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.moviesite.model.Movie;
import project.moviesite.service.CommentService;
import project.moviesite.service.MovieService;
import project.moviesite.service.UserService;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final MovieService movieService;
    private final UserService userService;
    private final CommentService commentService;

    public AdminController(MovieService movieService,
                           UserService userService,
                           CommentService commentService) {
        this.movieService = movieService;
        this.userService = userService;
        this.commentService = commentService;
    }

    // Add a new movie
    @PostMapping("/movies")
    @PreAuthorize("hasRole('client_admin')")
    public Movie addMovie(@RequestBody Movie movie) {
        // You might want to add validation logic here
        return movieService.saveMovie(movie);
    }

    // Delete a comment (admin-only)
    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasRole('client_admin')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    // Delete a user (admin-only)
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('client_admin')")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId, null); // Pass null as current user since it's admin action
        return ResponseEntity.ok().build();
    }

    // Export movies to JSON
    @GetMapping("/export/movies")
    @PreAuthorize("hasRole('client_admin')")
    public ResponseEntity<ByteArrayResource> exportMoviesToJson() throws IOException {
        ByteArrayResource resource = movieService.exportMoviesAsJSONResource();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/json"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=movies.json")
                .body(resource);
    }

    // Delte movie by ID
    @DeleteMapping("/movies/{movieId}")
    @PreAuthorize("hasRole('client_admin')")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long movieId) {
        movieService.deleteMovie(movieId);
        return ResponseEntity.ok().build();
    }
}
