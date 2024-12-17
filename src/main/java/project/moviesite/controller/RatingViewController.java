package project.moviesite.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.moviesite.service.RatingService;

@Controller
public class RatingViewController {
    private final RatingService ratingService;

    public RatingViewController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/rate-movie")
    public String rateMovie(
            @RequestParam Long movieId, @RequestParam int stars, @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String userSub = principal.getAttribute("sub");
        ratingService.addOrUpdateRating(userSub, movieId, stars);

        return "redirect:/movie-details/" + movieId;
    }

    @PostMapping("/delete-rating")
    public String deleteRating(@RequestParam Long movieId, @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String userSub = principal.getAttribute("sub");
        ratingService.deleteRating(userSub, movieId);

        return "redirect:/movie-details/" + movieId;
    }
}
