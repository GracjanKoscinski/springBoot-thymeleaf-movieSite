package project.moviesite.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import project.moviesite.model.Movie;
import project.moviesite.model.User;
import project.moviesite.repository.MovieRepository;
import project.moviesite.repository.UserRepository;

import java.util.List;
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
        User user = getUserBySub(userSub);
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with ID: " + movieId));

        user.getWatchlistMovies().add(movie);
        userRepository.save(user);
    }

    public void removeMovieFromWatchlist(String userSub, Long movieId) {
        User user = getUserBySub(userSub);
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with ID: " + movieId));

        if (!user.getWatchlistMovies().contains(movie)) {
            throw new EntityNotFoundException("Movie not found in user's watchlist");
        }

        user.getWatchlistMovies().remove(movie);
        userRepository.save(user);
    }

    public void addMovieToFavorites(String userSub, Long movieId) {
        User user = getUserBySub(userSub);
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with ID: " + movieId));

        user.getFavoriteMovies().add(movie);
        userRepository.save(user);
    }

    public void removeMovieFromFavorites(String userSub, Long movieId) {
        User user = getUserBySub(userSub);
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with ID: " + movieId));

        if (!user.getFavoriteMovies().contains(movie)) {
            throw new EntityNotFoundException("Movie not found in user's favorites");
        }

        user.getFavoriteMovies().remove(movie);
        userRepository.save(user);
    }

    public void addMovieToIgnored(String userSub, Long movieId) {
        User user = getUserBySub(userSub);
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with ID: " + movieId));

        user.getIgnoredMovies().add(movie);
        userRepository.save(user);
    }

    public void removeMovieFromIgnored(String userSub, Long movieId) {
        User user = getUserBySub(userSub);
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with ID: " + movieId));

        if (!user.getIgnoredMovies().contains(movie)) {
            throw new EntityNotFoundException("Movie not found in user's ignored list");
        }

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
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUser(String userId, String currentUserId) {
        if (userId.equals(currentUserId)) {
            throw new IllegalArgumentException("Cannot delete your own account");
        }
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    public User getUserBySub(String userSub) {
        return userRepository.findBySub(userSub);
    }
}
