package project.moviesite.controller;


import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.moviesite.model.Comment;
import project.moviesite.model.Movie;
import project.moviesite.model.User;
import project.moviesite.service.CommentService;
import project.moviesite.service.MovieService;
import project.moviesite.service.UserService;

@Controller
public class CommentViewController {
    private final UserService userService;
    private final MovieService movieService;
    private final CommentService commentService;

    public CommentViewController(UserService userService, MovieService movieService, CommentService commentService) {
        this.userService = userService;
        this.movieService = movieService;
        this.commentService = commentService;
    }

    @PostMapping("/comments/add")
    public String addComment(@Valid @ModelAttribute("comment") Comment comment,
                             BindingResult bindingResult,
                             @RequestParam Long movieId,
                             @AuthenticationPrincipal OAuth2User principal,
                             Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        Movie movie = movieService.getMovieById(movieId);
        comment.setMovie(movie);

        if (bindingResult.hasErrors()) {
            // Dodajemy wszystkie potrzebne atrybuty do modelu
            model.addAttribute("movie", movie);
            // Sprawdzamy czy użytkownik jest zalogowany i dodajemy odpowiednie flagi
            User user = userService.getUser(principal.getAttribute("sub"));
            model.addAttribute("isInWatchlist", user.getWatchlistMovies().contains(movie));
            model.addAttribute("isFavorite", user.getFavoriteMovies().contains(movie));
            model.addAttribute("isIgnored", user.getIgnoredMovies().contains(movie));

            return "movie-details"; // Zwracamy bezpośrednio widok zamiast przekierowania
        }

        String userId = principal.getAttribute("sub");
        User user = userService.getUser(userId);

        commentService.addComment(comment.getText(), user, movie);
        return "redirect:/movie-details/" + movieId;
    }
    @PostMapping("/comments/delete/{id}")
    public String deleteComment(@PathVariable Long id, @AuthenticationPrincipal OAuth2User principal) {
        Comment comment = commentService.getCommentById(id);
        if (comment == null) {
            return "redirect:/movies-view";
        }

        boolean isAdmin = isAdmin(principal);
        if (isAdmin || comment.getUser().getSub().equals(principal.getAttribute("sub"))) {
            commentService.deleteComment(id);
        }

        return "redirect:/movie-details/" + comment.getMovie().getId();
    }

    private boolean isAdmin(OAuth2User principal) {
        return principal.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_CLIENT_ADMIN"));
    }
}