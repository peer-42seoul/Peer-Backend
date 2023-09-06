package peer.backend.service.profile;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.entity.achievement.Achievement;
import peer.backend.entity.user.User;
import peer.backend.entity.user.UserAchievement;
import peer.backend.entity.user.UserLink;
import peer.backend.repository.achievement.AchievementRepository;
import peer.backend.repository.user.UserRepository;

@DisplayName("Profile Service Test")
@SpringBootTest()
@Transactional
class ProfileServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AchievementRepository achievementRepository;

    @Autowired
    private ProfileService profileService;

    @BeforeEach
    public void delete() {
        userRepository.deleteAll();
        achievementRepository.deleteAll();
    }

//    @Test
//    public void 업적생성() {
//        for (long i = 1; i <= 10; i++)
//        {
//            Achievement achievement = new Achievement(i, "업적" + i, "업적" + i + "에 대한 설명");
//            achievementRepository.save(achievement);
//        }
//    }

    @Test
    public void 유저생성() {
        User user1 = User.builder()
            .id(1L)
            .userId("cjswl1357")
            .password("12345")
            .name("이용훈")
            .email("cjswl1357@naver.com")
            .nickname("용훈쓰")
            .birthday(LocalDate.of(1997, 10, 13))
            .isAlarm(false)
            .phone("010-8331-2849")
            .address("노량진")
            .imageUrl("12345.jpg")
            .certification(true)
            .company("42서울")
            .introduce("기뭐링")
            .peerLevel(2L)
            .representAchievement("The Kimchi")
            .build();

        List<UserLink> userLinks = new ArrayList<>();
        for (long i = 1; i <= 2; i++)
        {
            UserLink userLink = UserLink.builder()
                .user(user1)
                .linkName("Github" + i)
                .linkUrl("Kimchi.github.com")
                .build();
            userLinks.add(userLink);
        }

        for (long i = 1; i <= 10; i++)
        {
            Achievement achievement = new Achievement(i, "업적" + i, "업적" + i + "에 대한 설명");
            achievementRepository.save(achievement);
        }

        List<Achievement> achievements = achievementRepository.findAll();
        userRepository.save(user1);
//        assertThat(achievements.size()).isEqualTo(10);
        List<UserAchievement> userAchievements = new ArrayList<>();
        userAchievements.add(new UserAchievement(user1.getId(), achievements.get(3).getId(), user1, achievements.get(3), LocalDateTime.now()));
        userAchievements.add(new UserAchievement(user1.getId(), achievements.get(5).getId(), user1, achievements.get(5), LocalDateTime.now()));
        userAchievements.add(new UserAchievement(user1.getId(), achievements.get(8).getId(), user1, achievements.get(8), LocalDateTime.now()));
        assertThat(userAchievements.size()).isEqualTo(3);
        user1.setUserLinks(userLinks);
        user1.setUserAchievements(userAchievements);

        assertThat(user1.getUserAchievements().size()).isEqualTo(3);
    }



//
//    User user1;
//
//    @Test
//    public void 유저셋팅() throws Exception {
//
//        List<Achievement> achievements = new ArrayList<>();
//        for (long i = 1; i <= 10; i++)
//        {
//            Achievement achievement = new Achievement(i, "업적" + i, "업적" + i + "에 대한 설명");
//            achievements.add(achievement);
//        }
//
//
//
////        for (Achievement ach : achievements)
////        {
////            System.out.println("ach.getName() = " + ach.getName() + ", ach.getDes() = " + ach.getDescription());
////        }
//
////        for (UserAchievement userAch : userAchievements)
////        {
////            System.out.println("userAch = " + userAch.getAchievement().getDescription());
////        }
//
//        assertThat(user1.getName()).isEqualTo("이용훈");
//    }


//    @Test
//    public void 다른사람_프로필_조회() throws Exception {
//
//    }

//    @Test
//    public void 나의_프로필_조회() throws Exception {
//        profileService.showMyProfile(user1.getId());
//
//    }

//    @Test
//    public void 나의_프로필_수정() throws Exception {
//
//    }
}