package project.moviesite.controller;

import java.util.Set;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import project.moviesite.model.Movie;
import project.moviesite.service.UserService;

@Controller
public class UserViewController {
    private final UserService userService;

    public UserViewController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user-info")
    public String userInfo(OAuth2AuthenticationToken authentication, Model model) {
        if (authentication == null) {
            return "access-denied";
        } else {
            String userSub = authentication.getPrincipal().getAttribute("sub");
            model.addAttribute("name", authentication.getPrincipal().getAttribute("name"));
            model.addAttribute("email", authentication.getPrincipal().getAttribute("email"));
            model.addAttribute("username", authentication.getPrincipal().getAttribute("preferred_username"));
            model.addAttribute("authorities", authentication.getAuthorities());
            Set<Movie> favoriteMovies = userService.getFavoriteMovies(userSub);
            Set<Movie> watchlistMovies = userService.getWatchlistMovies(userSub);
            Set<Movie> ignoredMovies = userService.getIgnoredMovies(userSub);

            model.addAttribute("favoriteMovies", favoriteMovies);
            model.addAttribute("watchlistMovies", watchlistMovies);
            model.addAttribute("ignoredMovies", ignoredMovies);
        }
        return "user-info";
    }
}
