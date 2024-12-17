package project.moviesite.controller.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import project.moviesite.model.Comment;
import project.moviesite.model.Movie;
import project.moviesite.model.Rating;
import project.moviesite.model.User;
import project.moviesite.service.CommentService;
import project.moviesite.service.MovieService;
import project.moviesite.service.RatingService;
import project.moviesite.service.UserService;

@RestController
@RequestMapping("/api/protected")
@PreAuthorize("hasRole('client_user')")
public class UserMovieInteractionController {
    private final UserService userService;
    private final RatingService ratingService;
    private final CommentService commentService;
    private final MovieService movieService;

    public UserMovieInteractionController(
            UserService userService,
            RatingService ratingService,
            CommentService commentService,
            MovieService movieService) {
        this.userService = userService;
        this.ratingService = ratingService;
        this.commentService = commentService;
        this.movieService = movieService;
    }

    @PostMapping("/{movieId}/rate")
    public ResponseEntity<?> rateMovie(@PathVariable Long movieId, @RequestParam int stars) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userSub = authentication instanceof JwtAuthenticationToken jwtAuth
                ? jwtAuth.getToken().getClaimAsString("sub")
                : authentication.getName();

        Rating rating = ratingService.addOrUpdateRating(userSub, movieId, stars);
        return ResponseEntity.ok(rating);
    }

    @DeleteMapping("/{movieId}/rate")
    public ResponseEntity<Void> deleteRating(@PathVariable Long movieId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userSub = authentication instanceof JwtAuthenticationToken jwtAuth
                ? jwtAuth.getToken().getClaimAsString("sub")
                : authentication.getName();

        ratingService.deleteRating(userSub, movieId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{movieId}/rating")
    public Rating getUserRating(@PathVariable Long movieId, Authentication authentication) {
        String userSub = authentication instanceof JwtAuthenticationToken jwtAuth
                ? jwtAuth.getToken().getClaimAsString("sub")
                : authentication.getName();

        return ratingService.getUserRatingForMovie(userSub, movieId);
    }

    @PostMapping("/{movieId}/comment")
    public ResponseEntity<?> addComment(@PathVariable Long movieId, @RequestParam String text) {
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

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
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

    @PostMapping("/watchlist/{movieId}")
    public ResponseEntity<Void> addToWatchlist(@PathVariable Long movieId, Authentication authentication) {
        String userSub = authentication instanceof JwtAuthenticationToken jwtAuth
                ? jwtAuth.getToken().getClaimAsString("sub")
                : authentication.getName();

        userService.addMovieToWatchlist(userSub, movieId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/watchlist/{movieId}")
    public ResponseEntity<Void> removeFromWatchlist(@PathVariable Long movieId, Authentication authentication) {
        String userSub = authentication instanceof JwtAuthenticationToken jwtAuth
                ? jwtAuth.getToken().getClaimAsString("sub")
                : authentication.getName();

        userService.removeMovieFromWatchlist(userSub, movieId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/favorites/{movieId}")
    public ResponseEntity<Void> addToFavorites(@PathVariable Long movieId, Authentication authentication) {
        String userSub = authentication instanceof JwtAuthenticationToken jwtAuth
                ? jwtAuth.getToken().getClaimAsString("sub")
                : authentication.getName();

        userService.addMovieToFavorites(userSub, movieId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/favorites/{movieId}")
    public ResponseEntity<Void> removeFromFavorites(@PathVariable Long movieId, Authentication authentication) {
        String userSub = authentication instanceof JwtAuthenticationToken jwtAuth
                ? jwtAuth.getToken().getClaimAsString("sub")
                : authentication.getName();

        userService.removeMovieFromFavorites(userSub, movieId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ignored/{movieId}")
    public ResponseEntity<Void> addToIgnored(@PathVariable Long movieId, Authentication authentication) {
        String userSub = authentication instanceof JwtAuthenticationToken jwtAuth
                ? jwtAuth.getToken().getClaimAsString("sub")
                : authentication.getName();

        userService.addMovieToIgnored(userSub, movieId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/ignored/{movieId}")
    public ResponseEntity<Void> removeFromIgnored(@PathVariable Long movieId, Authentication authentication) {
        String userSub = authentication instanceof JwtAuthenticationToken jwtAuth
                ? jwtAuth.getToken().getClaimAsString("sub")
                : authentication.getName();

        userService.removeMovieFromIgnored(userSub, movieId);
        return ResponseEntity.ok().build();
    }
}
