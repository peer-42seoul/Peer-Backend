package peer.backend.service.user;

import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import peer.backend.entity.user.User;
import peer.backend.entity.user.UserPushKeyword;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.user.UserPushKeywordRepository;
import peer.backend.repository.user.UserRepository;

@RequiredArgsConstructor
@Service
public class UserPushKeywordService {

    private final UserRepository userRepository;

    private final UserPushKeywordRepository userPushKeywordRepository;

    @Transactional
    public UserPushKeyword postKeyword(Long userId, String keyword) {
        User user = this.userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 유저 아이디 입니다!"));
        UserPushKeyword userPushKeyword = UserPushKeyword.builder()
            .user(user).keyword(keyword).build();
        return this.userPushKeywordRepository.save(userPushKeyword);
    }

    @Transactional
    public List<UserPushKeyword> getKeywordList(Long userId) {
        return this.userPushKeywordRepository
            .findAllByUserId(userId);
    }

    @Transactional
    public void deleteKeyword(Long id, String keyword) {
        this.userPushKeywordRepository.deleteByUserIdAndKeyword(id, keyword);
    }
}
