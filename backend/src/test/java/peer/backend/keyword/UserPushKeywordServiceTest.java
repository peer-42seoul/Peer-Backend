package peer.backend.keyword;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import peer.backend.entity.user.User;
import peer.backend.entity.user.UserPushKeyword;
import peer.backend.repository.user.UserPushKeywordRepository;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.user.UserPushKeywordService;

@ExtendWith(MockitoExtension.class)
@DisplayName("KeywordService Test")
public class UserPushKeywordServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPushKeywordRepository userPushKeywordRepository;

    @InjectMocks
    private UserPushKeywordService userPushKeywordService;

    @Test
    @DisplayName("postKeyword 함수 테스트")
    void postKeywordTest() {
        User user = User.builder()
            .id(1L)
            .name("test")
            .email("test@test.com")
            .nickname("test")
            .birthday(LocalDate.now())
            .isAlarm(false)
            .phone("test")
            .address("test")
            .certification(false)
            .company("test")
            .introduce("test")
            .peerLevel(0L)
            .representAchievement("test")
            .build();
        UserPushKeyword userPushKeyword = UserPushKeyword.builder()
            .user(user).keyword("hello").build();
        Optional<User> optionalUser = Optional.of(user);
        when(userRepository.findById(anyLong())).thenReturn(optionalUser);
        when(userPushKeywordRepository.save(any(UserPushKeyword.class))).thenReturn(
            userPushKeyword);

        UserPushKeyword ret = userPushKeywordService.postKeyword(user.getId(), "hello");

        assertEquals(ret.getKeyword(), userPushKeyword.getKeyword());
    }

    @Test
    @DisplayName("getKeyword 함수 테스트")
    void getKeywordTest() {
        List<UserPushKeyword> userPushKeywordList = new ArrayList<>();
        User user = User.builder()
            .name("test")
            .email("test@test.com")
            .nickname("test")
            .birthday(LocalDate.now())
            .isAlarm(false)
            .phone("test")
            .address("test")
            .certification(false)
            .company("test")
            .introduce("test")
            .peerLevel(0L)
            .representAchievement("test")
            .build();
        UserPushKeyword userPushKeyword = UserPushKeyword.builder()
            .user(user).keyword("hello").build();
        userPushKeywordList.add(userPushKeyword);

        when(userPushKeywordRepository.findAllByUserId(anyLong())).thenReturn(
            userPushKeywordList);
        List<UserPushKeyword> ret = userPushKeywordService.getKeywordList(1L);
        assertEquals(ret.get(0).getKeyword(), userPushKeyword.getKeyword());
    }

    @Test
    @DisplayName("deleteKeyword 함수 테스트")
    void deleteKeywordTest() {
        Long userId = 1L;
        String keyword = "test";

        userPushKeywordService.deleteKeyword(userId, keyword);

        verify(userPushKeywordRepository, times(1))
            .deleteByUserIdAndKeyword(userId, keyword);
    }
}
