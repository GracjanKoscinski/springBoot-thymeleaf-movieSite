package project.moviesite.service;

import org.springframework.stereotype.Service;
import project.moviesite.model.Movie;
import project.moviesite.model.User;
import project.moviesite.repository.MovieRepository;
import project.moviesite.repository.UserRepository;

import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    public UserService(UserRepository userRepository, MovieRepository movieRepository) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
    }

    public User getUser(String userSub) {
        return userRepository.findBySub(userSub);
    }

    public void addMovieToWatchlist(String userSub, Long movieId) {
        User user = userRepository.findBySub(userSub);
        Movie movie = movieRepository.findById(movieId).orElseThrow();
        user.getWatchlistMovies().add(movie);
        userRepository.save(user);
    }

    public void removeMovieFromWatchlist(String userSub, Long movieId) {
        User user = userRepository.findBySub(userSub);
        Movie movie = movieRepository.findById(movieId).orElseThrow();
        user.getWatchlistMovies().remove(movie);
        userRepository.save(user);
    }

    public void addMovieToFavorites(String userSub, Long movieId) {
        User user = userRepository.findBySub(userSub);
        Movie movie = movieRepository.findById(movieId).orElseThrow();
        user.getFavoriteMovies().add(movie);
        userRepository.save(user);
    }

    public void removeMovieFromFavorites(String userSub, Long movieId) {
        User user = userRepository.findBySub(userSub);
        Movie movie = movieRepository.findById(movieId).orElseThrow();
        user.getFavoriteMovies().remove(movie);
        userRepository.save(user);
    }

    public void addMovieToIgnored(String userSub, Long movieId) {
        User user = userRepository.findBySub(userSub);
        Movie movie = movieRepository.findById(movieId).orElseThrow();
        user.getIgnoredMovies().add(movie);
        userRepository.save(user);
    }

    public void removeMovieFromIgnored(String userSub, Long movieId) {
        User user = userRepository.findBySub(userSub);
        Movie movie = movieRepository.findById(movieId).orElseThrow();
        user.getIgnoredMovies().remove(movie);
        userRepository.save(user);
    }

    public Set<Movie> getFavoriteMovies(String userSub) {
        User user = userRepository.findBySub(userSub);
        return user.getFavoriteMovies();
    }

    public Set<Movie> getWatchlistMovies(String userSub) {
        User user = userRepository.findBySub(userSub);
        return user.getWatchlistMovies();
    }

    public Set<Movie> getIgnoredMovies(String userSub) {
        User user = userRepository.findBySub(userSub);
        return user.getIgnoredMovies();
    }
}