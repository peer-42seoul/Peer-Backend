package peer.backend.service.profile;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.profile.MyProfileResponse;
import peer.backend.entity.achievement.Achievement;
import peer.backend.entity.user.User;
import peer.backend.entity.user.UserAchievement;
import peer.backend.entity.user.UserLink;
import peer.backend.repository.achievement.AchievementRepository;
import peer.backend.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test ProfileServiceTest")
class ProfileServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ProfileService profileService;

    String email;
    String nickname;
    List<UserLink> linkList = new ArrayList<>();
    User user;
    @BeforeEach
    void beforeEach() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        email = "test@test.com";
        nickname = "test123";
        linkList.add(UserLink.builder()
                .id(1L)
                .linkName("test 1")
                .linkName("test 1")
                .build());
        user = User.builder()
                .id(1L)
                .password(encoder.encode("test1234"))
                .email(email)
                .nickname(nickname)
                .address("test address")
                .imageUrl("test image")
                .company("test company")
                .userLinks(linkList)
                .build();
    }

    @Test
    @DisplayName("Get profile Test")
    void getProfileTest() {
        when(userRepository.findByName(anyString())).thenReturn(Optional.of(user));
        MyProfileResponse ret = profileService.getProfile(email);
        assertThat(ret.getProfileImageUrl()).isEqualTo(user.getImageUrl());
        assertThat(ret.getNickname()).isEqualTo(user.getNickname());
        assertThat(ret.getEmail()).isEqualTo(user.getEmail());
        assertThat(ret.getCompany()).isEqualTo(user.getCompany());
        assertThat(ret.getIntroduction()).isEqualTo(user.getIntroduce());
    }
}
