package project.moviesite.service;

import org.springframework.stereotype.Service;
import project.moviesite.model.*;
import project.moviesite.repository.CommentRepository;
import project.moviesite.repository.MovieRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final CommentRepository commentRepository;

    public MovieService(MovieRepository movieRepository, CommentRepository commentRepository) {
        this.movieRepository = movieRepository;
        this.commentRepository = commentRepository;
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

}