package project.moviesite.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import project.moviesite.model.Movie;
import project.moviesite.repository.MovieRepository;
import project.moviesite.service.MovieService;
import project.moviesite.service.UserService;

import java.io.IOException;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    private final MovieService movieService;
    private final UserService userService;
    private static final String ACCESS_DENIED = "access-denied";
    public AdminViewController(MovieService movieService, UserService userService) {
        this.movieService = movieService;

        this.userService = userService;
    }

    private boolean isAdmin(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_CLIENT_ADMIN"));
    }

    @GetMapping("/panel")
    public String adminPanel(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if(principal == null) {
            return ACCESS_DENIED;
        }
        if (!isAdmin(principal)) {
            return ACCESS_DENIED;
        }
        model.addAttribute("movie", new Movie());
        model.addAttribute("directors", movieService.getUniqueDirectors());
        model.addAttribute("actors", movieService.getUniqueActors());
        model.addAttribute("genres", movieService.getUniqueGenres());
        model.addAttribute("users", userService.getAllUsers());
        return "admin-panel";
    }

    @PostMapping("/add-movie")
    public String addMovie(
            @Valid @ModelAttribute("movie") Movie movie,
            BindingResult bindingResult,
            @AuthenticationPrincipal OAuth2User principal,
            Model model
    ) {

        if (!isAdmin(principal)) {
            return ACCESS_DENIED;
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("directors", movieService.getUniqueDirectors());
            model.addAttribute("actors", movieService.getUniqueActors());
            model.addAttribute("genres", movieService.getUniqueGenres());
            return "admin-panel";
        }

        movieService.saveMovie(movie);
        return "redirect:/admin/panel?success";
    }

    @PostMapping("/delete-user")
    public String deleteUser(
            @RequestParam("userId") String userId,
            @AuthenticationPrincipal OAuth2User principal
    ) {
        if (!isAdmin(principal)) {
            return ACCESS_DENIED;
        }

        String currentUserId = principal.getAttribute("sub");

        try {
            userService.deleteUser(userId, currentUserId);
            return "redirect:/admin/panel?userDeleted";
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/panel?error=cannotDeleteSelf";
        } catch (EntityNotFoundException e) {
            return "redirect:/admin/panel?error=userNotFound";
        }
    }

    @GetMapping("/export-movies")
    public ResponseEntity<Resource> exportMovies(@AuthenticationPrincipal OAuth2User principal) {
        if (!isAdmin(principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            ByteArrayResource resource = movieService.exportMoviesAsJSONResource();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=movies.json")
                    .contentType(MediaType.APPLICATION_JSON)
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/delete-movie")
    public String deleteMovie(
            @RequestParam("movieId") Long movieId,
            @AuthenticationPrincipal OAuth2User principal
    ) {
        if (!isAdmin(principal)) {
            return ACCESS_DENIED;
        }

        movieService.deleteMovie(movieId);

        return "redirect:/admin/panel?movieDeleted";

    }

}
