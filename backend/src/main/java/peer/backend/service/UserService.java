package peer.backend.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import peer.backend.annotation.tracking.UserRegistrationTracking;
import peer.backend.entity.user.User;
import peer.backend.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @UserRegistrationTracking
    public User saveUser(User user) {
        return this.userRepository.save(user);
    }

}
