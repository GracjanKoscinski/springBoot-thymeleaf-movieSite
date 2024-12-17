package project.moviesite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.moviesite.model.Director;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {}
