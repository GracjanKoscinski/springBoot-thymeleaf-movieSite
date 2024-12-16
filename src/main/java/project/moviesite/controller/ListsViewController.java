package project.moviesite.controller;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import project.moviesite.service.UserService;

@Controller
public class ListsViewController {
    private final UserService userService;
    private static final String REDIRECT = "redirect:/movie-details/";

    public ListsViewController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/watchlist/add/{id}")
    public String addToWatchlist(@PathVariable Long id, @AuthenticationPrincipal OAuth2User principal) {
        userService.addMovieToWatchlist(principal.getAttribute("sub"), id);
        return REDIRECT + id;
    }

    @PostMapping("/watchlist/remove/{id}")
    public String removeFromWatchlist(@PathVariable Long id, @AuthenticationPrincipal OAuth2User principal) {
        userService.removeMovieFromWatchlist(principal.getAttribute("sub"), id);
        return REDIRECT + id;
    }

    @PostMapping("/favorites/add/{id}")
    public String addToFavorites(@PathVariable Long id, @AuthenticationPrincipal OAuth2User principal) {
        userService.addMovieToFavorites(principal.getAttribute("sub"), id);
        return REDIRECT + id;
    }

    @PostMapping("/favorites/remove/{id}")
    public String removeFromFavorites(@PathVariable Long id, @AuthenticationPrincipal OAuth2User principal) {
        userService.removeMovieFromFavorites(principal.getAttribute("sub"), id);
        return REDIRECT + id;
    }

    @PostMapping("/ignored/add/{id}")
    public String addToIgnored(@PathVariable Long id, @AuthenticationPrincipal OAuth2User principal) {
        userService.addMovieToIgnored(principal.getAttribute("sub"), id);
        return REDIRECT + id;
    }

    @PostMapping("/ignored/remove/{id}")
    public String removeFromIgnored(@PathVariable Long id, @AuthenticationPrincipal OAuth2User principal) {
        userService.removeMovieFromIgnored(principal.getAttribute("sub"), id);
        return REDIRECT + id;
    }
}
