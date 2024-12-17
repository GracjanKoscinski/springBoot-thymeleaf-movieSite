package project.moviesite.controller.rest;

import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import project.moviesite.dto.MovieCreateRequest;
import project.moviesite.model.Movie;
import project.moviesite.service.CommentService;
import project.moviesite.service.MovieService;
import project.moviesite.service.UserService;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('client_admin')")
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

    @PostMapping("/movies")
    public ResponseEntity<Movie> addMovie(@Valid @RequestBody MovieCreateRequest request) {
        Movie savedMovie = movieService.createMovie(request);
        return ResponseEntity.ok(savedMovie);
    }

    @DeleteMapping("/movies/{movieId}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long movieId) {
        movieService.deleteMovie(movieId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId, Authentication authentication) {
        String currentUserSub = authentication instanceof JwtAuthenticationToken jwtAuth ?
                jwtAuth.getToken().getClaimAsString("sub") :
                authentication.getName();

        if (userId.equals(currentUserSub)) {
            return ResponseEntity
                    .badRequest()
                    .body("Cannot delete your own account");
        }

        userService.deleteUser(userId, null);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/export/movies")
    public ResponseEntity<ByteArrayResource> exportMoviesToJson() throws IOException {
        ByteArrayResource resource = movieService.exportMoviesAsJSONResource();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/json"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=movies.json")
                .body(resource);
    }
}
