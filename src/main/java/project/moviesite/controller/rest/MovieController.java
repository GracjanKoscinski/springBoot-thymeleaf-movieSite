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
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class MovieController {
    private final MovieService movieService;
    private final UserService userService;

    public MovieController(MovieService movieService, UserService userService) {
        this.movieService = movieService;
        this.userService = userService;
    }

    @GetMapping("/movies")
    public List<Movie> getAllMovies(Authentication authentication) {
        List<Movie> movies = movieService.getMovies();

        if (authentication != null && authentication.isAuthenticated()) {
            String userSub = authentication instanceof JwtAuthenticationToken jwtAuth ?
                    jwtAuth.getToken().getClaimAsString("sub") :
                    authentication.getName();

            User user = userService.getUser(userSub);
            return movies.stream()
                    .filter(movie -> !user.getIgnoredMovies().contains(movie))
                    .collect(Collectors.toList());
        }

        return movies;
    }

    @GetMapping("/search")
    public List<Movie> searchMovies(
            @RequestParam(required = false) List<String> genres,
            @RequestParam(required = false) List<String> actors,
            @RequestParam(required = false) String director,
            @RequestParam(required = false) String sortBy,
            Authentication authentication) {

        List<Movie> filteredMovies = movieService.getMoviesByFilters(genres, actors, director);

        if (authentication != null && authentication.isAuthenticated()) {
            String userSub = authentication instanceof JwtAuthenticationToken jwtAuth ?
                    jwtAuth.getToken().getClaimAsString("sub") :
                    authentication.getName();

            User user = userService.getUser(userSub);
            filteredMovies = filteredMovies.stream()
                    .filter(movie -> !user.getIgnoredMovies().contains(movie))
                    .collect(Collectors.toList());
        }

        return movieService.sortMovies(filteredMovies, sortBy);
    }

    @GetMapping("/movie/{id}")
    public Movie getMovieById(@PathVariable Long id) {
        return movieService.getMovieById(id);
    }

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
}