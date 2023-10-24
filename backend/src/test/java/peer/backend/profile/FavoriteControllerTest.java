package peer.backend.profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import peer.backend.config.jwt.TokenProvider;
import peer.backend.controller.profile.FavoriteController;
import peer.backend.dto.profile.FavoritePage;
import peer.backend.entity.user.User;
import peer.backend.oauth.PrincipalDetails;
import peer.backend.service.profile.FavoriteService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Test FavoriteController")
public class FavoriteControllerTest {
//    MockMvc mvc;
//    @Autowired
//    TokenProvider tokenProvider;
//    @Mock
//    FavoriteService favoriteService;
//    @InjectMocks
//    FavoriteController favoriteController;
//    User user;
//    FavoritePage favoritePage;
//
//    @BeforeEach
//    void beforeEach() {
//        mvc = standaloneSetup(favoriteController).build();
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        user = User.builder()
//                .id(1L)
//                .email("test1@test.com")
//                .password(encoder.encode("testtest"))
//                .name("test1")
//                .nickname("test1")
//                .isAlarm(false)
//                .address("test address")
//                .build();
//    }
//
//    @Test
//    @DisplayName("Test getFavorite")
//    @WithUserDetails(value = "1", userDetailsServiceBeanName = "userDetailsServiceImpl", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    public void getFavoriteTest() throws Exception {
//        // given
//        String jwt = tokenProvider.createAccessToken(user);
//        // when
//        when(favoriteService.getFavorite(
//                any(PrincipalDetails.class),
//                anyString(),
//                anyInt(),
//                anyInt())
//        ).thenReturn(favoritePage);
//        // then
//        mvc.perform(get("/api/v1/recruit/favorite")
//                        .header("Authorization", "Bearer " + jwt)
//                        .param("type", "project")
//                        .param("page", String.valueOf(1))
//                        .param("pagesize", String.valueOf(10)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("Test deleteAll")
//    @WithUserDetails(value = "3", userDetailsServiceBeanName = "userDetailsServiceImpl", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    public void deleteAllTest() throws Exception {
//        // given
//        String jwt = tokenProvider.createAccessToken(user);
//        // then
//        mvc.perform(delete("/api/v1/recruit/favorite")
//                        .header("Authorization", "Bearer " + jwt)
//                        .param("type", "project")
//                )
//                .andExpect(status().isCreated());
//    }
}
