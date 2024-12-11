package project.moviesite.service;

import org.springframework.stereotype.Service;
import project.moviesite.model.Genre;
import project.moviesite.model.Movie;
import project.moviesite.repository.MovieRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> getMovies() {
        return movieRepository.findAll();
    }

    public Movie getMovies(Long id) {
        return movieRepository.findById(id).orElse(null);
    }

    public List<Movie> getMoviesByGenres(List<String> genres) {
        return movieRepository.findAll().stream()
                .filter(movie -> movie.getGenres().stream()
                        .map(Genre::getGenreName)
                        .collect(Collectors.toSet()).containsAll(genres))
                .collect(Collectors.toList());
    }

    public List<Genre> getUniqueGenres() {
        return movieRepository.findAll().stream()
                .flatMap(movie -> movie.getGenres().stream())
                .distinct()
                .collect(Collectors.toList());
    }

}