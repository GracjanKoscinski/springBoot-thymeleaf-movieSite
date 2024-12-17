package project.moviesite.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.moviesite.model.User;
import project.moviesite.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserSyncService {

    private final UserRepository userRepository;

    public void syncUser(String sub, String username, String email, String firstName, String lastName) {
        User user = userRepository.findById(sub).orElse(new User());
        user.setSub(sub);
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        userRepository.save(user);
    }
}
