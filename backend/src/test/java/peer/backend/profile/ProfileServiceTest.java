package peer.backend.profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.util.FieldUtils;
import org.springframework.test.context.TestPropertySource;
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
import peer.backend.service.profile.ProfileService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private FileService fileService;
    @InjectMocks
    private ProfileService profileService;

    private String filepath;

    String email;
    String nickname;
    String name;
    String imagePath;
    String imageName;
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
        imagePath = "src/test/java/peer/backend/profile/image";
        imageName = UUID.randomUUID().toString();
        PrincipalDetails details = new PrincipalDetails(user);
        auth = new UsernamePasswordAuthenticationToken(details, details.getPassword(), details.getAuthorities());
        ReflectionTestUtils.setField(profileService, "filepath", "/Users/juhyelee/profile/image");
    }

    @Test
    @DisplayName("Get profile Test")
    void getProfileTest() {
        MyProfileResponse ret = profileService.getProfile(auth);
        assertThat(ret.getProfileImageUrl()).isEqualTo(user.getImageUrl());
        assertThat(ret.getNickname()).isEqualTo(user.getNickname());
        assertThat(ret.getEmail()).isEqualTo(user.getEmail());
        assertThat(ret.getCompany()).isEqualTo(user.getCompany());
        assertThat(ret.getIntroduction()).isEqualTo(user.getIntroduce());
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
    @DisplayName("Edit profile Test")
    void editProfileTest() throws IOException {
        when(fileService.saveFile(any(MultipartFile.class), anyString(), anyString())).thenReturn(filepath + "/" + imageName + ".png");
        when(fileService.updateFile(any(MultipartFile.class), anyString(), anyString())).thenReturn(filepath + "/" + imageName + ".png");
        // 추가
        FileInputStream newInputStream = new FileInputStream(imagePath + "/test1.png");
        MultipartFile multipartFile = new MockMultipartFile("test1", "test1.png", "image", newInputStream);
        EditProfileRequest profile = EditProfileRequest.builder()
                .profileImage(multipartFile)
                .imageChange(false)
                .nickname(user.getNickname())
                .introduction(user.getIntroduce())
                .build();
        profileService.editProfile(auth, profile);
        assertThat(user.getImageUrl()).isEqualTo(filepath + "/" + imageName + ".png");
        // 변경
        FileInputStream fileInputStream = new FileInputStream(imagePath + "/test1.png");
        multipartFile = new MockMultipartFile("test1", "test1.png", "image", fileInputStream);
        profile = EditProfileRequest.builder()
                .profileImage(multipartFile)
                .imageChange(false)
                .nickname(user.getNickname())
                .introduction(user.getIntroduce())
                .build();
        profileService.editProfile(auth, profile);
        assertThat(user.getImageUrl()).isEqualTo(filepath + "/" + imageName + ".png");
        // 변경 안함
        MockMultipartFile emptyFile = new MockMultipartFile("empty", "empty.png", "image", new byte[0]);
        profile = EditProfileRequest.builder()
                .profileImage(emptyFile)
                .imageChange(false)
                .nickname(user.getNickname())
                .introduction(user.getIntroduce())
                .build();
        profileService.editProfile(auth, profile);
        assertThat(user.getImageUrl()).isNotNull();
        // 삭제
        profile = EditProfileRequest.builder()
                .profileImage(emptyFile)
                .imageChange(true)
                .nickname(user.getNickname())
                .introduction(user.getIntroduce())
                .build();
        profileService.editProfile(auth, profile);
        assertThat(user.getImageUrl()).isNull();
    }
}
