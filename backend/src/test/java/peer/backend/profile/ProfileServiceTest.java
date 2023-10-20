package peer.backend.profile;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.apache.tika.Tika;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import peer.backend.service.profile.ProfileService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test ProfileServiceTest")
class ProfileServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserLinkRepository userLinkRepository;
    @Mock
    private Tika tika;
    @InjectMocks
    private ProfileService profileService;

    @Value("${custom.filePath}")
    private String filePath;

    String email;
    String nickname;
    String name;
    String imagePath;
    List<UserLink> linkList = new ArrayList<>();
    User user;
    PrincipalDetails principal;
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
        imagePath = "src/test/java/peer/backend/profile/image";
        principal = new PrincipalDetails(user);
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

    @Test
    @DisplayName("Edit links Test")
    void editLinksTest() {
        when(userRepository.findByName(anyString())).thenReturn(Optional.of(user));
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
        profileService.editLinks(email, newList);
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
    @DisplayName("Edit profile Test")
    void editProfileTest() throws IOException {
        when(tika.detect(any(InputStream.class))).thenReturn("image");
        // 없는 상태 에서 추가
        FileInputStream fileInputStream = new FileInputStream(imagePath + "/test1.png");
        MultipartFile multipartFile = new MockMultipartFile("test1", "test1.png", "image", fileInputStream);
        EditProfileRequest profile = EditProfileRequest.builder()
                .profileImage(multipartFile)
                .imageChange(false)
                .nickname(user.getNickname())
                .introduction(user.getIntroduce())
                .build();
        profileService.editProfile(principal, profile);
        String imageUrl = user.getImageUrl().substring(52);
        assertThat(imageUrl).isEqualTo(filePath + "/upload/profiles/" + user.getId() + "/profile.png");
        // 있는 상태 에서 변경
        fileInputStream = new FileInputStream(imagePath + "/test2.png");
        multipartFile = new MockMultipartFile("test2", "test2.png", "image", fileInputStream);
        profile = EditProfileRequest.builder()
                .profileImage(multipartFile)
                .imageChange(false)
                .nickname(user.getNickname())
                .introduction(user.getIntroduce())
                .build();
        profileService.editProfile(principal, profile);
        imageUrl = user.getImageUrl().substring(52);
        assertThat(imageUrl).isEqualTo(filePath + "/upload/profiles/" + user.getId() + "/profile.png");
        // 있는 상태 에서 변경 하지 않음
        MockMultipartFile emptyFile = new MockMultipartFile("empty", "empty.png", "image", new byte[0]);
        profile = EditProfileRequest.builder()
                .profileImage(emptyFile)
                .imageChange(false)
                .nickname(user.getNickname())
                .introduction(user.getIntroduce())
                .build();
        profileService.editProfile(principal, profile);
        imageUrl = user.getImageUrl().substring(52);
        assertThat(imageUrl).isEqualTo(filePath + "/upload/profiles/" + user.getId() + "/profile.png");
        // 삭제
        profile = EditProfileRequest.builder()
                .profileImage(emptyFile)
                .imageChange(true)
                .nickname(user.getNickname())
                .introduction(user.getIntroduce())
                .build();
        profileService.editProfile(principal, profile);
        assertThat(user.getImageUrl()).isNull();
    }
}
