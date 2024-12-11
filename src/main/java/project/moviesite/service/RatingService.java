package project.moviesite.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.moviesite.model.Movie;
import project.moviesite.model.Rating;
import project.moviesite.model.User;
import project.moviesite.repository.RatingRepository;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final UserService userService;
    private final MovieService movieService;

    public RatingService(RatingRepository ratingRepository,
                         UserService userService,
                         MovieService movieService) {
        this.ratingRepository = ratingRepository;
        this.userService = userService;
        this.movieService = movieService;
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
        Movie movie = movieService.getMovieById(movieId);

        Rating newRating = new Rating();
        newRating.setUser(user);
        newRating.setMovie(movie);
        newRating.setStarsRating(stars);

        return ratingRepository.save(newRating);
    }

    public Rating getUserRatingForMovie(String userSub, Long movieId) {
        return ratingRepository.findByUserSubAndMovieId(userSub, movieId);
    }

    public double getAverageRatingForMovie(Long movieId) {
        return ratingRepository.findAll().stream()
                .filter(rating -> rating.getMovie().getId().equals(movieId))
                .mapToInt(Rating::getStarsRating)
                .average()
                .orElse(0.0);
    }
    @Transactional
    public void deleteRating(String userSub, Long movieId) {
        Rating existingRating = ratingRepository.findByUserSubAndMovieId(userSub, movieId);
        if (existingRating != null) {
            ratingRepository.delete(existingRating);
        }
    }
}