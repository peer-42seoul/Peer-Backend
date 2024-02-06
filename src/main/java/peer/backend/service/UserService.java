package peer.backend.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import peer.backend.entity.user.User;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User saveUser(User user) {
        return this.userRepository.save(user);
    }

    public User findByEmail(String email) {
        return this.userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("존재하지 않은 유저입니다."));
    }

    public User findById(Long id) {
        return this.userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("존재하지 않은 유저입니다."));
    }

    public Page<User> getUserListFromPageable(Pageable pageable) {
        return this.userRepository.findAll(pageable);
    }

    public Page<User> searchUserListByNicknameFromPageable(Pageable pageable, String keyword) {
        return this.userRepository.findByNicknameContainingFromPageable(pageable, keyword);
    }
}
