package project.moviesite.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.moviesite.model.Movie;
import project.moviesite.model.Rating;
import project.moviesite.model.User;
import project.moviesite.repository.MovieRepository;
import project.moviesite.repository.RatingRepository;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final UserService userService;
    private final MovieRepository movieRepository; // Inject repository instead

    public RatingService(RatingRepository ratingRepository, UserService userService, MovieRepository movieRepository) {
        this.ratingRepository = ratingRepository;
        this.userService = userService;
        this.movieRepository = movieRepository;
    }

    @Transactional
    public Rating addOrUpdateRating(String userSub, Long movieId, int stars) {
        // Find existing rating or create new one
        Rating existingRating = ratingRepository.findByUserSubAndMovieId(userSub, movieId);

        if (existingRating != null) {
            // Update existing rating
            existingRating.setStarsRating(stars);
            return ratingRepository.save(existingRating);
        }

        // Create new rating
        User user = userService.getUser(userSub);
        Movie movie =
                movieRepository.findById(movieId).orElseThrow(() -> new IllegalArgumentException("Movie not found"));

        Rating newRating = new Rating();
        newRating.setUser(user);
        newRating.setMovie(movie);
        newRating.setStarsRating(stars);

        return ratingRepository.save(newRating);
    }

    public Rating getUserRatingForMovieView(String userSub, Long movieId) {
        return ratingRepository.findByUserSubAndMovieId(userSub, movieId);
    }

    public Rating getUserRatingForMovie(String userSub, Long movieId) {
        Rating rating = ratingRepository.findByUserSubAndMovieId(userSub, movieId);
        if (rating == null) {
            throw new EntityNotFoundException("Rating not found for user and movie combination");
        }
        return rating;
    }

    public double getAverageRatingForMovie(Long movieId) {
        return ratingRepository.findAll().stream()
                .filter(rating -> rating.getMovie().getId().equals(movieId))
                .mapToInt(Rating::getStarsRating)
                .average()
                .orElse(0.0);
    }

    public void deleteRating(String userSub, Long movieId) {
        Rating rating = ratingRepository.findByUserSubAndMovieId(userSub, movieId);
        if (rating == null) {
            throw new EntityNotFoundException("Rating not found for user and movie combination");
        }
        ratingRepository.delete(rating);
    }
}
