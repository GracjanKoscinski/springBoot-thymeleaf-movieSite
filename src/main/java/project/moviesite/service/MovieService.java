package project.moviesite.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import project.moviesite.model.*;
import project.moviesite.repository.MovieRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final RatingService ratingService;

    public MovieService(MovieRepository movieRepository, RatingService ratingService) {
        this.movieRepository = movieRepository;
        this.ratingService = ratingService;
    }

    public List<Movie> getMovies() {
        return movieRepository.findAll();
    }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Movie not found with ID: " + id));
    }

    public List<Movie> getMoviesByFilters(List<String> genres, List<String> actors, String director) {
        return movieRepository.findAll().stream()
                .filter(movie -> genres == null || genres.isEmpty() || movie.getGenres().stream()
                        .map(Genre::getGenreName)
                        .collect(Collectors.toSet()).containsAll(genres))
                .filter(movie -> actors == null || actors.isEmpty() || movie.getActors().stream()
                        .map(Actor::getName)
                        .collect(Collectors.toSet()).containsAll(actors))
                .filter(movie -> director == null || director.isEmpty() || director.equals(movie.getDirector().getName()))
                .collect(Collectors.toList());
    }

    public List<Movie> sortMovies(List<Movie> movies, String sortBy) {
        if (sortBy == null || sortBy.isEmpty()) {
            return movies;
        }

        switch (sortBy) {
            case "year_asc":
                return movies.stream()
                        .sorted(Comparator.comparingInt(Movie::getYear))
                        .collect(Collectors.toList());
            case "year_desc":
                return movies.stream()
                        .sorted(Comparator.comparingInt(Movie::getYear).reversed())
                        .collect(Collectors.toList());
            case "runtime_asc":
                return movies.stream()
                        .sorted(Comparator.comparingInt(Movie::getRuntime))
                        .collect(Collectors.toList());
            case "runtime_desc":
                return movies.stream()
                        .sorted(Comparator.comparingInt(Movie::getRuntime).reversed())
                        .collect(Collectors.toList());
            case "rating_asc":
                return movies.stream()
                        .sorted(Comparator.comparingDouble(movie ->
                                ratingService.getAverageRatingForMovie(movie.getId())))
                        .collect(Collectors.toList());
            case "rating_desc":
                return movies.stream()
                        .sorted((movie1, movie2) -> {
                            double rating1 = ratingService.getAverageRatingForMovie(movie1.getId());
                            double rating2 = ratingService.getAverageRatingForMovie(movie2.getId());
                            return Double.compare(rating2, rating1);  // Reversed order for descending
                        })
                        .collect(Collectors.toList());
            default:
                return movies;
        }
    }

    public List<Genre> getUniqueGenres() {
        return movieRepository.findAll().stream()
                .flatMap(movie -> movie.getGenres().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Actor> getUniqueActors() {
        return movieRepository.findAll().stream()
                .flatMap(movie -> movie.getActors().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Director> getUniqueDirectors() {
        return movieRepository.findAll().stream()
                .map(Movie::getDirector)
                .distinct()
                .collect(Collectors.toList());
    }


    public ByteArrayResource exportMoviesAsJSONResource() throws IOException {
        List<Movie> movies = movieRepository.findAll();

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(movies);

        return new ByteArrayResource(json.getBytes(StandardCharsets.UTF_8));
    }

    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public boolean deleteMovie(Long movieId) {
        if (movieRepository.existsById(movieId)) {
            movieRepository.deleteById(movieId);
            return true;
        }
        return false;
    }
}