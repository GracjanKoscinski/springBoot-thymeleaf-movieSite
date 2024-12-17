package project.moviesite.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import project.moviesite.model.Movie;
import project.moviesite.model.User;
import project.moviesite.repository.MovieRepository;
import project.moviesite.repository.RatingRepository;
import project.moviesite.repository.UserRepository;

@Service
public class RankingService {
    private final MovieService movieService;
    private final UserService userService;
    private final RatingService ratingService;
    private final CommentService commentService;
    private final MovieRepository movieRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;

    public RankingService(
            MovieService movieService,
            UserService userService,
            RatingService ratingService,
            CommentService commentService,
            MovieRepository movieRepository,
            RatingRepository ratingRepository,
            UserRepository userRepository) {
        this.movieService = movieService;
        this.userService = userService;
        this.ratingService = ratingService;
        this.commentService = commentService;
        this.movieRepository = movieRepository;
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
    }

    public List<Map<String, Object>> getTopMoviesByRating() {
        List<Movie> allMovies = movieService.getMovies();

        return allMovies.stream()
                .map(movie -> {
                    double avgRating = ratingService.getAverageRatingForMovie(movie.getId());
                    return Map.of(
                            "movie", movie,
                            "avgRating", avgRating);
                })
                .sorted((a, b) -> Double.compare((double) b.get("avgRating"), (double) a.get("avgRating")))
                .limit(5)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getTopUsersByComments() {
        List<User> allUsers = userService.getAllUsers();

        return allUsers.stream()
                .map(user -> {
                    long commentCount = commentService.countCommentsByUser(user);
                    return Map.of(
                            "user", user,
                            "commentCount", commentCount);
                })
                .sorted((a, b) -> Long.compare((long) b.get("commentCount"), (long) a.get("commentCount")))
                .limit(5)
                .collect(Collectors.toList());
    }

    public List<Movie> getMoviesWithAboveAverageRating() {
        return movieRepository.findMoviesWithAboveAverageRating();
    }

    public List<Object[]> findAverageRatingsByGenre() {
        return ratingRepository.findAverageRatingsByGenre();
    }

    public List<Map<String, Object>> getUsersWithMostRatingsByGenre(String genreName) {
        if (!movieService.getUniqueGenres().stream()
                .anyMatch(genre -> genre.getGenreName().equals(genreName))) {
            throw new EntityNotFoundException("Genre not found: " + genreName);
        }

        List<Object[]> results = userRepository.findTopUsersByGenreRatings(genreName);

        return results.stream()
                .map(result -> {
                    Map<String, Object> userStats = new HashMap<>();
                    userStats.put("username", result[0]);
                    userStats.put("ratingCount", ((Number) result[1]).longValue());
                    return userStats;
                })
                .collect(Collectors.toList());
    }
}
