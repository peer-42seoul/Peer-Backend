package peer.backend.File;

import java.io.InputStream;
import java.time.LocalDate;

import org.apache.tika.Tika;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import peer.backend.dto.profile.response.UserImageResponse;
import peer.backend.entity.user.User;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.file.ProfileImageService;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileImageServiceTest {

    @Mock
    Tika tika;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    ProfileImageService profileImageService;

    User user;

    @BeforeEach
    void setting() {
        user = User.builder()
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
            .imageUrl("/hello")
            .build();
    }

    @Test
    public void setting_test() {
        when(userRepository.count()).thenReturn(1L);
        assertThat(userRepository.count()).isEqualTo(1L);
    }

    @Test
    @DisplayName("회원의 프로필 이미지를 local에 저장한다.")
    void updateProfileImg() throws IOException {
        Optional<User> opUser = Optional.of(user);
        when(tika.detect(any(InputStream.class))).thenReturn("image");
        when(userRepository.findById(anyLong())).thenReturn(opUser);

        FileInputStream fileInputStream = new FileInputStream("src/test/java/peer/backend/File/testImage.png");
        MultipartFile multipartFile = new MockMultipartFile("abcd", "abcd.png", "image/png",
                fileInputStream);

        UserImageResponse result = profileImageService.saveProfileImage(multipartFile, 1L);
        String compare = result.getImageUrl().substring(result.getImageUrl().lastIndexOf("upload"));
        assertThat(compare).isEqualTo("upload/profiles/1/profile.png");
    }

    @Test
    @DisplayName("회원의 프로필 이미지 url을 가져온다.")
    void getProfileImg() {
        Optional<User> opUser = Optional.of(user);
        when(userRepository.findById(anyLong())).thenReturn(opUser);

        String result = profileImageService.getProfileImageUrl(1L);

        assertThat(result).isEqualTo("/hello");
    }

    @Test
    @DisplayName("회원 프로필 이미지 삭제하고 db의 url정보 삭제")
    void deleteProfile() throws IOException {
        Optional<User> opUser = Optional.of(user);
        when(userRepository.findById(anyLong())).thenReturn(opUser);
        profileImageService.deleteProfileIamge(1L);
        assertThat(user.getImageUrl()).isEqualTo(null);
    }
}