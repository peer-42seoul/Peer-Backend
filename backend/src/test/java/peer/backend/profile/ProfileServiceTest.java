package peer.backend.profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import peer.backend.dto.profile.request.EditProfileRequest;
import peer.backend.dto.profile.request.UserLinkRequest;
import peer.backend.dto.profile.response.MyProfileResponse;
import peer.backend.dto.profile.response.OtherProfileResponse;
import peer.backend.entity.user.User;
import peer.backend.entity.user.UserLink;
import peer.backend.oauth.PrincipalDetails;
import peer.backend.repository.user.UserLinkRepository;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.file.FileService;
import peer.backend.service.file.ObjectService;
import peer.backend.service.profile.ProfileService;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test ProfileServiceTest")
class ProfileServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserLinkRepository userLinkRepository;
    @Mock
    private ObjectService objectService;
    @InjectMocks
    private ProfileService profileService;

    String email;
    String nickname;
    String name;
    List<UserLink> linkList = new ArrayList<>();
    User user;
    Authentication auth;
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
                .company("test company")
                .userLinks(linkList)
                .build();
        PrincipalDetails details = new PrincipalDetails(user);
        auth = new UsernamePasswordAuthenticationToken(details, details.getPassword(), details.getAuthorities());
    }

    @Test
    @DisplayName("Get profile Test")
    void getProfileTest() {
        MyProfileResponse ret = profileService.getProfile(auth);
        assertThat(ret.getProfileImageUrl()).isEqualTo(user.getImageUrl());
        assertThat(ret.getNickname()).isEqualTo(user.getNickname());
        assertThat(ret.getEmail()).isEqualTo(user.getEmail());
        assertThat(ret.getAssociation()).isEqualTo(user.getCompany());
        assertThat(ret.getIntroduction()).isEqualTo(user.getIntroduce() == null ? "" : user.getIntroduce());
    }

    @Test
    @DisplayName("Edit links Test")
    void editLinksTest() {
        List<UserLinkRequest> newList = new ArrayList<>();
        newList.add(
                UserLinkRequest.builder()
                        .linkName("new link 1")
                        .linkUrl("new link 1")
                        .build()
        );
        newList.add(
                UserLinkRequest.builder()
                        .linkName("new link 2")
                        .linkUrl("new link 2")
                        .build()
        );
        profileService.editLinks(auth, newList);
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
        OtherProfileResponse ret = profileService.getOtherProfile(user.getId(), info);
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

    @Test
    @DisplayName("Edit profile Test add")
    void editProfileTestAdd() throws IOException {
        when(objectService.uploadObject(anyString(), anyString(), anyString())).thenReturn("new image");
        FileInputStream newInputStream = new FileInputStream("src/test/java/peer/backend/profile/image/test1.png");
        MultipartFile multipartFile = new MockMultipartFile("test1", "test1.png", "image", newInputStream);
        EditProfileRequest profile = EditProfileRequest.builder()
                .profileImage(multipartFile)
                .imageChange("FALSE")
                .nickname(user.getNickname())
                .introduction(user.getIntroduce())
                .build();
        profileService.editProfile(auth, profile, false);
        assertThat(user.getImageUrl()).isEqualTo("new image");
    }

    @Test
    @DisplayName("Edit profile Test update 1")
    void editProfileTestUpdate1() throws IOException {
        user.setImageUrl("test image");
        when(objectService.uploadObject(anyString(), anyString(), anyString())).thenReturn("other image");
        FileInputStream fileInputStream = new FileInputStream("src/test/java/peer/backend/profile/image/test1.png");
        MultipartFile multipartFile = new MockMultipartFile("test1", "test1.png", "image", fileInputStream);
        EditProfileRequest profile = EditProfileRequest.builder()
                .profileImage(multipartFile)
                .imageChange("FALSE")
                .nickname(user.getNickname())
                .introduction(user.getIntroduce())
                .build();
        profileService.editProfile(auth, profile, false);
        assertThat(user.getImageUrl()).isEqualTo("other image");
    }

    @Test
    @DisplayName("Edit profile Test update 2")
    void editProfileUpdate2() throws IOException {
        user.setImageUrl("test image");
        MockMultipartFile emptyFile = new MockMultipartFile("empty", "empty.png", "image", new byte[0]);
        EditProfileRequest profile = EditProfileRequest.builder()
                .profileImage(emptyFile)
                .imageChange("FALSE")
                .nickname(user.getNickname())
                .introduction(user.getIntroduce())
                .build();
        profileService.editProfile(auth, profile, false);
        assertThat(user.getImageUrl()).isEqualTo("test image");
    }

    @Test
    @DisplayName("Edit profile Test delete")
    void editProfileDelete() throws IOException {
        MockMultipartFile emptyFile = new MockMultipartFile("empty", "empty.png", "image", new byte[0]);
        EditProfileRequest profile = EditProfileRequest.builder()
                .profileImage(emptyFile)
                .imageChange("TRUE")
                .nickname(user.getNickname())
                .introduction(user.getIntroduce())
                .build();
        profileService.editProfile(auth, profile, true);
        assertThat(user.getImageUrl()).isNull();
    }
}
