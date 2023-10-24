package peer.backend.profile;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import peer.backend.dto.profile.request.UserLinkDTO;
import peer.backend.dto.profile.response.MyProfileResponse;
import peer.backend.dto.profile.response.OtherProfileDto;
import peer.backend.entity.user.User;
import peer.backend.entity.user.UserLink;
import peer.backend.oauth.PrincipalDetails;
import peer.backend.repository.user.UserLinkRepository;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.profile.ProfileService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test ProfileServiceTest")
class ProfileServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserLinkRepository userLinkRepository;
    @InjectMocks
    private ProfileService profileService;

    String email;
    String nickname;
    String name;
    List<UserLink> linkList = new ArrayList<>();
    User user;
    PrincipalDetails principalDetails;
    @BeforeEach
    void beforeEach() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        email = "test@email.com";
        nickname = "test nickname";
        name = "test name";
        linkList.add(UserLink.builder()
                .id(1L)
                .linkName("test 1")
                .linkName("test 1")
                .build());
        user = User.builder()
                .id(1L)
                .password(encoder.encode("test password"))
                .name(name)
                .email(email)
                .nickname(nickname)
                .address("test address")
                .imageUrl("test image")
                .company("test company")
                .userLinks(linkList)
                .build();
        principalDetails = new PrincipalDetails(user);
    }

    @Test
    @DisplayName("Get profile Test")
    void getProfileTest() {
        MyProfileResponse ret = profileService.getProfile(principalDetails);
        assertThat(ret.getProfileImageUrl()).isEqualTo(user.getImageUrl());
        assertThat(ret.getNickname()).isEqualTo(user.getNickname());
        assertThat(ret.getEmail()).isEqualTo(user.getEmail());
        assertThat(ret.getCompany()).isEqualTo(user.getCompany());
        assertThat(ret.getIntroduction()).isEqualTo(user.getIntroduce());
    }

    @Test
    @DisplayName("Edit links Test")
    void editLinksTest() {
        List<UserLinkDTO> newList = new ArrayList<>();
        newList.add(
                UserLinkDTO.builder()
                        .linkName("new link 1")
                        .linkUrl("new link 1")
                        .build()
        );
        newList.add(
                UserLinkDTO.builder()
                        .linkName("new link 2")
                        .linkUrl("new link 2")
                        .build()
        );
        profileService.editLinks(principalDetails, newList);
        assertThat(user.getUserLinks().get(0).getLinkName()).isEqualTo("new link 1");
        assertThat(user.getUserLinks().get(0).getLinkUrl()).isEqualTo("new link 1");
        assertThat(user.getUserLinks().get(1).getLinkName()).isEqualTo("new link 2");
        assertThat(user.getUserLinks().get(1).getLinkUrl()).isEqualTo("new link 2");
    }

    @Test
    @DisplayName("Get other profile Test")
    void getOtherProfileTest() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        List<String> info = new ArrayList<>();
        info.add("nickname");
        info.add("profileImageUrl");
        OtherProfileDto ret = profileService.getOtherProfile(user.getId(), info);
        assertThat(ret.getNickname()).isEqualTo(user.getNickname());
        assertThat(ret.getProfileImageUrl()).isEqualTo(user.getImageUrl());
        info.clear();
        info.add("nickname");
        ret = profileService.getOtherProfile(user.getId(), info);
        assertThat(ret.getNickname()).isEqualTo(user.getNickname());
        info.clear();
        info.add("profileImageUrl");
        ret = profileService.getOtherProfile(user.getId(), info);
        assertThat(ret.getProfileImageUrl()).isEqualTo(user.getImageUrl());
    }
}
