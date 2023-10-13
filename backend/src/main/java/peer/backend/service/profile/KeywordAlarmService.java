package peer.backend.service.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.entity.user.User;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class KeywordAlarmService {
    private final UserRepository userRepository;

    @Transactional
    public void addKeyword(String name, String newKeyword) {
        User user = userRepository.findByName(name).orElseThrow(
                () -> new NotFoundException("사용자가 존재하지 않습니다.")
        );
        String keyword = String.format("%s^&%%s", user.getKeywordAlarm(), newKeyword);
        user.setKeywordAlarm(keyword);
        userRepository.save(user);
    }
}