package project.moviesite.service;

import org.springframework.stereotype.Service;
import project.moviesite.model.Movie;
import project.moviesite.model.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RankingService {
    private final MovieService movieService;
    private final UserService userService;
    private final RatingService ratingService;
    private final CommentService commentService;

    public RankingService(
            MovieService movieService,
            UserService userService,
            RatingService ratingService,
            CommentService commentService
    ) {
        this.movieService = movieService;
        this.userService = userService;
        this.ratingService = ratingService;
        this.commentService = commentService;
    }

    public List<Map<String, Object>> getTopMoviesByRating() {
        List<Movie> allMovies = movieService.getMovies();

        return allMovies.stream()
                .map(movie -> {
                    double avgRating = ratingService.getAverageRatingForMovie(movie.getId());
                    return Map.of(
                            "movie", movie,
                            "avgRating", avgRating
                    );
                })
                .sorted((a, b) -> Double.compare((double)b.get("avgRating"), (double)a.get("avgRating")))
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
                            "commentCount", commentCount
                    );
                })
                .sorted((a, b) -> Long.compare((long)b.get("commentCount"), (long)a.get("commentCount")))
                .limit(5)
                .collect(Collectors.toList());
    }
}
