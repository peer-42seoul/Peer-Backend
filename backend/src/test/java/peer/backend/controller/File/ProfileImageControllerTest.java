package peer.backend.controller.File;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import peer.backend.entity.user.User;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.file.ProfileImageService;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Import(S3MockConfig.class)
//@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ProfileImageControllerTest {

    @Mock
    private AmazonS3 amazonS3;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    ProfileImageService profileImageService;

    @Autowired
    private S3Mock s3Mock;

    @AfterEach
    public void tearDown() {
        s3Mock.stop();
    }

    User user;

    @BeforeEach
    void setting() {
        user = User.builder()
                .id(1L)
                .name("test")
                .email("test@test.com")
                .nickname("test")
                .birthday(LocalDateTime.now())
                .is_alarm(false)
                .phone("test")
                .address("test")
                .certification(false)
                .company("test")
                .introduce("test")
                .peerLevel(0L)
                .representAchievement("test")
                .build();
    }

    @Test
    public void setting_test() {
        when(userRepository.count()).thenReturn(1L);
        assertThat(userRepository.count()).isEqualTo(1L);
    }

    @Test
    @DisplayName("회원의 프로필 이미지를 변경하면 s3 서버와 연동하여 이미지를 업로드한다")
    void updateProfileImg() throws IOException {
        Optional<User> opUser = Optional.of(user);
        FileInputStream fileInputStream = new FileInputStream("/Users/jwee/Postman/files/abcd.png");
        MultipartFile multipartFile = new MockMultipartFile("abcd", "abcd.png", "image/png", fileInputStream);
        when(userRepository.findById(anyLong())).thenReturn(opUser);
        when(amazonS3.putObject(any(),any(),any(),any())).thenReturn(new PutObjectResult());
        when(amazonS3.getUrl(any(),any())).thenReturn(new URL("https://hello.world.com"));
//
//
        String result = profileImageService.saveProfileImage(multipartFile, 1L);
        assertThat(result).isEqualTo("https://hello.world.com");
    }
}