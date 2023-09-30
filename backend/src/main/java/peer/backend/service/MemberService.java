package peer.backend.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import peer.backend.annotation.tracking.UserRegistrationTracking;
import peer.backend.dto.security.UserInfo;
import peer.backend.entity.user.User;
import peer.backend.exception.UnauthorizedException;
import peer.backend.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final UserRepository repository;

    @UserRegistrationTracking
    public User signUp(UserInfo info) {
        Optional<User> checkUser = repository.findByNickname(info.getNickname());
        if (checkUser.isPresent()) {
            throw new UnauthorizedException("이미 존재하는 닉네임입니다.");
        }
        checkUser = repository.findByEmail(info.getEmail());
        if (checkUser.isPresent()) {
            throw new UnauthorizedException("이미 존재하는 이메일입니다.");
        }
        User user = info.convertUser();
        return repository.save(user);
    }
}
