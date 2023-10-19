package peer.backend.profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.controller.profile.FavoriteController;
import peer.backend.dto.profile.FavoriteResponse;
import peer.backend.entity.user.User;
import peer.backend.service.profile.FavoriteService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test FavoriteController")
public class FavoriteControllerTest {
    @Mock
    private FavoriteService favoriteService;
    @InjectMocks
    private FavoriteController favoriteController;

    User user;
    Page<FavoriteResponse> page;

    private static class TestPrincipal implements Principal {
        private final User user;

        public TestPrincipal(User user) {
            this.user = user;
        }

        @Override
        public String getName() {
            return user.getName();
        }
    }

    TestPrincipal testPrincipal;

    @BeforeEach
    @Transactional
    void beforeEach() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user = User.builder()
                .id(20L)
                .email("test@email.com")
                .password(encoder.encode("test password"))
                .name("test name")
                .nickname("test nickname")
                .isAlarm(false)
                .address("test address")
                .imageUrl("test image URL")
                .build();
        List<FavoriteResponse> favoriteResponseList = new ArrayList<>();
        for (int index = 0; index < 3; index++) {
            FavoriteResponse favoriteResponse = FavoriteResponse.builder()
                    .postId((long)index + 1)
                    .title("test title " + index)
                    .image("test image " + index)
                    .userId((long)index * 4)
                    .userNickname("test nickname " + index * 4)
                    .userImage("test image " + index * 4)
                    .isFavorite(true)
                    .build();
            favoriteResponseList.add(favoriteResponse);
        }
        Pageable pageable = PageRequest.of(1, 10);
        page = new PageImpl<>(favoriteResponseList, pageable, favoriteResponseList.size());
        testPrincipal = new TestPrincipal(user);
    }

    @Test
    @DisplayName("Test getFavorite")
    public void getFavoriteTest() {
        when(favoriteService.getFavorite(anyString(), anyString(), anyInt(), anyInt())).thenReturn(page);
        ResponseEntity<Object> ret = favoriteController.getFavorite(testPrincipal, "project", 1, 10);
        assertThat(ret.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Test deleteAll")
    public void deleteAllTest() {
        ResponseEntity<Object> ret = favoriteController.deleteAll(testPrincipal, "study");
        assertThat(ret.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
