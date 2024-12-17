package project.moviesite.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import project.moviesite.dto.MovieCreateRequest;
import project.moviesite.model.*;
import project.moviesite.repository.ActorRepository;
import project.moviesite.repository.DirectorRepository;
import project.moviesite.repository.GenreRepository;
import project.moviesite.repository.MovieRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final RatingService ratingService;
    private final DirectorRepository directorRepository;
    private final GenreRepository genreRepository;
    private final ActorRepository actorRepository;

    public MovieService(MovieRepository movieRepository, RatingService ratingService, DirectorRepository directorRepository, GenreRepository genreRepository, ActorRepository actorRepository) {
        this.movieRepository = movieRepository;
        this.ratingService = ratingService;
        this.directorRepository = directorRepository;
        this.genreRepository = genreRepository;
        this.actorRepository = actorRepository;
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

    public void deleteMovie(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new EntityNotFoundException("Movie not found with id: " + movieId);
        }
        movieRepository.deleteById(movieId);
    }
    @Transactional
    public Movie createMovie(MovieCreateRequest request) {
        Movie movie = new Movie();
        movie.setTitle(request.getTitle());
        movie.setYear(request.getYear());
        movie.setRuntime(request.getRuntime());
        movie.setPlot(request.getPlot());
        movie.setPosterUrl(request.getPosterUrl());

        // Ustawianie reżysera
        Director director = directorRepository.findById(request.getDirectorId())
                .orElseThrow(() -> new EntityNotFoundException("Director not found with id: " + request.getDirectorId()));
        movie.setDirector(director);

        // Ustawianie gatunków
        if (request.getGenreIds() != null) {
            Set<Genre> genres = request.getGenreIds().stream()
                    .map(id -> genreRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("Genre not found with id: " + id)))
                    .collect(Collectors.toSet());
            movie.setGenres(genres);
        }

        // Ustawianie aktorów
        if (request.getActorIds() != null) {
            Set<Actor> actors = request.getActorIds().stream()
                    .map(id -> actorRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("Actor not found with id: " + id)))
                    .collect(Collectors.toSet());
            movie.setActors(actors);
        }

        return movieRepository.save(movie);
    }
}
