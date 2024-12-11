package project.moviesite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.moviesite.model.User;

public interface UserRepository extends JpaRepository<User, String> {
    User findBySub(String sub);
}
