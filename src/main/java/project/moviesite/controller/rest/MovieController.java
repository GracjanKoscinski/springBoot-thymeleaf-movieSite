package project.moviesite.controller.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import project.moviesite.model.*;
import project.moviesite.service.CommentService;
import project.moviesite.service.MovieService;
import project.moviesite.service.RatingService;
import project.moviesite.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MovieController {
    private final MovieService movieService;
    private final RatingService ratingService;
    private final UserService userService;
    private final CommentService commentService;

    public MovieController(MovieService movieService,
                           RatingService ratingService,
                           UserService userService, CommentService commentService) {
        this.movieService = movieService;
        this.ratingService = ratingService;
        this.userService = userService;
        this.commentService = commentService;
    }

    // Get all movies
    @GetMapping("/movies")
    public List<Movie> getAllMovies() {
        return movieService.getMovies();
    }

    // Search movies with filters
    @GetMapping("/search")
    public List<Movie> searchMovies(
            @RequestParam(required = false) List<String> genres,
            @RequestParam(required = false) List<String> actors,
            @RequestParam(required = false) String director,
            @RequestParam(required = false) String sortBy) {
        List<Movie> filteredMovies = movieService.getMoviesByFilters(genres, actors, director);
        return movieService.sortMovies(filteredMovies, sortBy);
    }

    // Get movie by ID
    @GetMapping("/movie/{id}")
    public Movie getMovieById(@PathVariable Long id) {
        return movieService.getMovieById(id);
    }

    // Unique filters endpoints
    @GetMapping("/genres")
    public List<Genre> getUniqueGenres() {
        return movieService.getUniqueGenres();
    }

    @GetMapping("/actors")
    public List<Actor> getUniqueActors() {
        return movieService.getUniqueActors();
    }

    @GetMapping("/directors")
    public List<Director> getUniqueDirectors() {
        return movieService.getUniqueDirectors();
    }

    @PostMapping("/protected/{movieId}/rate")
    @PreAuthorize("hasRole('client_user')")
    public ResponseEntity<?> rateMovie(
            @PathVariable Long movieId,
            @RequestParam int stars
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userSub = authentication instanceof JwtAuthenticationToken jwtAuth
                ? jwtAuth.getToken().getClaimAsString("sub")
                : authentication.getName();

        Rating rating = ratingService.addOrUpdateRating(userSub, movieId, stars);
        return ResponseEntity.ok(rating);
    }

    @DeleteMapping("/protected/{movieId}/rate")
    @PreAuthorize("hasRole('client_user')")
    public ResponseEntity<Void> deleteRating(
            @PathVariable Long movieId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userSub = authentication instanceof JwtAuthenticationToken jwtAuth
                ? jwtAuth.getToken().getClaimAsString("sub")
                : authentication.getName();

        ratingService.deleteRating(userSub, movieId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/protected/{movieId}/rating")
    @PreAuthorize("hasRole('client_user')")
    public Rating getUserRating(
            @PathVariable Long movieId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userSub = authentication instanceof JwtAuthenticationToken jwtAuth
                ? jwtAuth.getToken().getClaimAsString("sub")
                : authentication.getName();

        return ratingService.getUserRatingForMovie(userSub, movieId);
    }

    @PostMapping("/protected/{movieId}/comment")
    @PreAuthorize("hasRole('client_user')")
    public ResponseEntity<?> addComment(
            @PathVariable Long movieId,
            @RequestParam String text
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userSub = authentication instanceof JwtAuthenticationToken jwtAuth
                ? jwtAuth.getToken().getClaimAsString("sub")
                : authentication.getName();

        User user = userService.getUserBySub(userSub);
        Movie movie = movieService.getMovieById(movieId);

        if (movie == null || user == null) {
            return ResponseEntity.badRequest().body("Invalid user or movie");
        }

        commentService.addComment(text, user, movie);
        return ResponseEntity.ok("Comment added successfully");
    }

    @DeleteMapping("/protected/comment/{commentId}")
    @PreAuthorize("hasRole('client_user')")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long commentId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userSub = authentication instanceof JwtAuthenticationToken jwtAuth
                ? jwtAuth.getToken().getClaimAsString("sub")
                : authentication.getName();

        User user = userService.getUserBySub(userSub);
        Comment comment = commentService.getCommentById(commentId);

        if (comment == null || !comment.getUser().equals(user)) {
            return ResponseEntity.status(403).body("You are not authorized to delete this comment");
        }

        commentService.deleteComment(commentId);
        return ResponseEntity.ok("Comment deleted successfully");
    }
}