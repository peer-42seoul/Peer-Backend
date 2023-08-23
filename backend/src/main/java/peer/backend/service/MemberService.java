package peer.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import peer.backend.dto.security.Message;
import peer.backend.dto.security.UserInfo;
import peer.backend.entity.user.User;
import peer.backend.repository.user.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final UserRepository repository;
    private final BCryptPasswordEncoder encoder;

    public Message signUp(UserInfo login) {
        Optional<User> checkUser = repository.findByNickname(login.getNickname());
        if (checkUser.isPresent()) {
            return new Message(HttpStatus.UNAUTHORIZED, "이미 존재하는 닉네임입니다.");
        }
        checkUser = repository.findByEmail(login.getEmail());
        if (checkUser.isPresent()) {
            return new Message(HttpStatus.UNAUTHORIZED, "이미 존재하는 이메일입니다.");
        }
        // email 인증 진행
        repository.save(login.convertUser());
        return new Message(HttpStatus.CREATED);
    }
}
