package project.moviesite.controller.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.moviesite.model.Movie;
import project.moviesite.service.UserService;

@RestController
@RequestMapping("/api")
public class UserProfileController {
    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/protected/user-info")
    @PreAuthorize("hasRole('client_user')")
    public ResponseEntity<Map<String, Object>> getUserInfo(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(403).build();
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userSub = jwt.getSubject();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("sub", userSub);
        userInfo.put("email", jwt.getClaim("email"));
        userInfo.put("name", jwt.getClaim("name"));
        userInfo.put("preferred_username", jwt.getClaim("preferred_username"));
        userInfo.put("authorities", authentication.getAuthorities());

        Set<Movie> favoriteMovies = userService.getFavoriteMovies(userSub);
        Set<Movie> watchlistMovies = userService.getWatchlistMovies(userSub);
        Set<Movie> ignoredMovies = userService.getIgnoredMovies(userSub);

        userInfo.put("favoriteMovies", favoriteMovies);
        userInfo.put("watchlistMovies", watchlistMovies);
        userInfo.put("ignoredMovies", ignoredMovies);

        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/protected/users/{sub}/info")
    @PreAuthorize("hasRole('client_admin')")
    public ResponseEntity<Map<String, Object>> getUserInfoBySub(@PathVariable String sub) {
        Map<String, Object> userInfo = new HashMap<>();

        Set<Movie> favoriteMovies = userService.getFavoriteMovies(sub);
        Set<Movie> watchlistMovies = userService.getWatchlistMovies(sub);
        Set<Movie> ignoredMovies = userService.getIgnoredMovies(sub);

        userInfo.put("sub", sub);
        userInfo.put("favoriteMovies", favoriteMovies);
        userInfo.put("watchlistMovies", watchlistMovies);
        userInfo.put("ignoredMovies", ignoredMovies);

        return ResponseEntity.ok(userInfo);
    }
}
