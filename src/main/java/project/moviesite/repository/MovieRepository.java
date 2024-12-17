package project.moviesite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.moviesite.model.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long> {}
