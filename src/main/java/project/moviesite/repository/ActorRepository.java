package project.moviesite.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.moviesite.model.Actor;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {
}

