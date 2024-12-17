package project.moviesite.controller.rest;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.moviesite.model.Movie;
import project.moviesite.service.RankingService;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {
    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/top-movies")
    public List<Map<String, Object>> getTopMovies() {
        return rankingService.getTopMoviesByRating();
    }

    @GetMapping("/top-users")
    public List<Map<String, Object>> getTopUsers() {
        return rankingService.getTopUsersByComments();
    }

    @GetMapping("/top-genres")
    public List<Object[]> getTopGenres() {
        return rankingService.findAverageRatingsByGenre();
    }

    @GetMapping("/movies-above-average")
    public List<Movie> getMoviesAboveAverage() {
        return rankingService.getMoviesWithAboveAverageRating();
    }

    @GetMapping("/users-by-genre/{genreName}/ratings")
    public ResponseEntity<List<Map<String, Object>>> getUsersWithMostRatingsByGenre(@PathVariable String genreName) {
        List<Map<String, Object>> stats = rankingService.getUsersWithMostRatingsByGenre(genreName);
        return ResponseEntity.ok(stats);
    }
}
