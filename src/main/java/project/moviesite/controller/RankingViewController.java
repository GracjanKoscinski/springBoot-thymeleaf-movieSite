package project.moviesite.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import project.moviesite.model.Movie;
import project.moviesite.model.User;
import project.moviesite.service.RankingService;
import project.moviesite.service.RatingService;

@Controller
public class RankingViewController {
    private final RankingService rankingService;
    private final RatingService ratingService;

    public RankingViewController(RankingService rankingService, RatingService ratingService) {
        this.rankingService = rankingService;
        this.ratingService = ratingService;
    }

    @GetMapping("/ranking")
    public String showRanking(Model model) {
        List<Movie> topMovies = rankingService.getTopMoviesByRating().stream()
                .map(m -> (Movie) m.get("movie"))
                .collect(Collectors.toList());

        Map<Long, Double> movieAverageRatings = topMovies.stream()
                .collect(
                        Collectors.toMap(Movie::getId, movie -> ratingService.getAverageRatingForMovie(movie.getId())));

        List<User> topUsers = rankingService.getTopUsersByComments().stream()
                .map(u -> (User) u.get("user"))
                .collect(Collectors.toList());

        model.addAttribute("topMovies", topMovies);
        model.addAttribute("movieAverageRatings", movieAverageRatings);
        model.addAttribute("topUsers", topUsers);
        return "ranking";
    }
}
