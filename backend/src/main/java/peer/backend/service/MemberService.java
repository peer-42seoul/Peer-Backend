package peer.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import peer.backend.dto.security.Message;
import peer.backend.dto.security.UserInfo;
import peer.backend.entity.user.User;
import peer.backend.exception.UnauthorizedException;
import peer.backend.oauth.PrincipalDetails;
import peer.backend.repository.user.UserRepository;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final UserRepository repository;

    public Message signUp(UserInfo info) {
        Optional<User> checkUser = repository.findByNickname(info.getNickname());
        if (checkUser.isPresent()) {
            throw new UnauthorizedException("이미 존재하는 닉네임입니다.");
        }
        checkUser = repository.findByEmail(info.getEmail());
        if (checkUser.isPresent()) {
            throw new UnauthorizedException("이미 존재하는 이메일입니다.");
        }
        User user = info.convertUser();
        repository.save(user);
        return new Message(HttpStatus.CREATED);
    }
}
