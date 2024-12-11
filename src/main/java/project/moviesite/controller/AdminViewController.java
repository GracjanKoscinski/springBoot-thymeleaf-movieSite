package project.moviesite.controller;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import project.moviesite.model.Movie;
import project.moviesite.repository.MovieRepository;
import project.moviesite.service.MovieService;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    private final MovieService movieService;
    private final MovieRepository movieRepository;

    public AdminViewController(MovieService movieService, MovieRepository movieRepository) {
        this.movieService = movieService;
        this.movieRepository = movieRepository;
    }
    private boolean isAdmin(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_CLIENT_ADMIN"));
    }
    @GetMapping("/panel")
    public String adminPanel(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (!isAdmin(principal)) {
            return "redirect:/access-denied";
        }
        model.addAttribute("movie", new Movie());
        model.addAttribute("directors", movieService.getUniqueDirectors());
        model.addAttribute("actors", movieService.getUniqueActors());
        model.addAttribute("genres", movieService.getUniqueGenres());

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
            return "redirect:/access-denied";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("directors", movieService.getUniqueDirectors());
            model.addAttribute("actors", movieService.getUniqueActors());
            model.addAttribute("genres", movieService.getUniqueGenres());
            return "admin-panel";
        }

        movieRepository.save(movie);
        return "redirect:/admin/panel?success";
    }
}