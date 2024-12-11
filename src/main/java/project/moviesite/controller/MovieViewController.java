package project.moviesite.controller;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import project.moviesite.model.*;
import project.moviesite.service.MovieService;
import project.moviesite.service.RatingService;
import project.moviesite.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MovieViewController {

    private final UserService userService;
    private final MovieService movieService;
    private final RatingService ratingService;

    public MovieViewController(UserService userService, MovieService movieService, RatingService ratingService) {
        this.userService = userService;
        this.movieService = movieService;
        this.ratingService = ratingService;
    }

    @GetMapping("/movies-view")
    public String showMovies(@RequestParam(required = false) List<String> genres,
                             @RequestParam(required = false) List<String> actors,
                             @RequestParam(required = false) String director,
                             @RequestParam(required = false) String search,
                             Model model,
                             @AuthenticationPrincipal OAuth2User principal) {

        List<Movie> movies;

        // Jeśli użytkownik jest zalogowany
        if (principal != null) {
            String userSub = principal.getAttribute("sub");
            User user = userService.getUser(userSub);

            // Pobierz wszystkie filmy z filtracją
            movies = movieService.getMoviesByFilters(genres, actors, director);

            // Usuń zignorowane filmy
            movies = movies.stream()
                    .filter(movie -> !user.getIgnoredMovies().contains(movie))
                    .collect(Collectors.toList());
        } else {
            // Dla niezalogowanego użytkownika bez zmian
            movies = movieService.getMoviesByFilters(genres, actors, director);
        }

        // Dodatkowe filtrowanie po wyszukiwaniu
        if (search != null && !search.isEmpty()) {
            movies = movies.stream()
                    .filter(movie -> movie.getTitle().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        }

        List<Genre> uniqueGenres = movieService.getUniqueGenres();
        List<Actor> uniqueActors = movieService.getUniqueActors();
        List<Director> uniqueDirectors = movieService.getUniqueDirectors();

        if (genres == null) genres = new ArrayList<>();
        if (actors == null) actors = new ArrayList<>();

        model.addAttribute("movies", movies);
        model.addAttribute("selectedGenres", genres);
        model.addAttribute("selectedActors", actors);
        model.addAttribute("selectedDirector", director);
        model.addAttribute("uniqueGenres", uniqueGenres);
        model.addAttribute("uniqueActors", uniqueActors);
        model.addAttribute("uniqueDirectors", uniqueDirectors);
        model.addAttribute("searchQuery", search);

        return "movies";
    }


    @GetMapping("/movie-details/{id}")
    public String movieDetails(@PathVariable Long id, Model model, @AuthenticationPrincipal OAuth2User principal) {
        Movie movie = movieService.getMovieById(id);
        if (movie == null) {
            return "redirect:/movies-view";
        }

        User user = principal != null ? userService.getUser(principal.getAttribute("sub")) : null;

        boolean isInWatchlist = user != null && user.getWatchlistMovies().contains(movie);
        boolean isFavorite = user != null && user.getFavoriteMovies().contains(movie);
        boolean isIgnored = user != null && user.getIgnoredMovies().contains(movie);


        if (user != null) {
            Rating userRating = ratingService.getUserRatingForMovie(user.getSub(), id);
            model.addAttribute("userRating", userRating);
        }

        double averageRating = ratingService.getAverageRatingForMovie(id);

        model.addAttribute("movie", movie);
        model.addAttribute("isInWatchlist", isInWatchlist);
        model.addAttribute("isFavorite", isFavorite);
        model.addAttribute("isIgnored", isIgnored);
        model.addAttribute("averageRating", averageRating);

        return "movie-details";
    }

}