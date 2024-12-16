package project.moviesite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.moviesite.model.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
}

