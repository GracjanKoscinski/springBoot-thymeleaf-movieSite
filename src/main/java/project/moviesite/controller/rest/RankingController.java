package project.moviesite.controller.rest;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
