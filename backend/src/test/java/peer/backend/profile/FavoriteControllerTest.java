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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.config.jwt.TokenProvider;
import peer.backend.controller.profile.FavoriteController;
import peer.backend.dto.profile.FavoritePage;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.RecruitFavorite;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.entity.team.enums.TeamUserRoleType;
import peer.backend.entity.user.User;
import peer.backend.oauth.PrincipalDetails;
import peer.backend.service.profile.FavoriteService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Test FavoriteController")
public class FavoriteControllerTest {

    MockMvc mvc;
    @Autowired
    TokenProvider tokenProvider;
    @Mock
    FavoriteService favoriteService;
    @InjectMocks
    FavoriteController favoriteController;
    User user;
    FavoritePage favoritePage;
    @BeforeEach
    @Transactional
    void beforeEach() {
        mvc = standaloneSetup(favoriteController).build();
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
        List<RecruitFavorite> recruitFavoriteList = new ArrayList<>();
        List<TeamUser> teamUserList = new ArrayList<>();
        for (int index = 0; index < 12; index++) {
            User newUser = User.builder()
                    .id((long)index + 1)
                    .name("test " + index)
                    .nickname("test " + index)
                    .imageUrl("test " + index)
                    .build();
            TeamUser teamUser = TeamUser.builder()
                    .user(newUser)
                    .role(index % 4 == 0 ? TeamUserRoleType.LEADER : TeamUserRoleType.MEMBER)
                    .build();
            teamUserList.add(teamUser);
        }
        for (int index = 0; index < 3; index++) {
            Team team = Team.builder()
                    .teamUsers(teamUserList.subList(index * 4, index * 4 + 3))
                    .type(index % 2 == 0 ? TeamType.PROJECT : TeamType.STUDY)
                    .build();
            RecruitStatus status;
            if (index == 0) {
                status = RecruitStatus.BEFORE;
            }
            else if (index == 1) {
                status = RecruitStatus.ONGOING;
            }
            else {
                status = RecruitStatus.DONE;
            }
            Recruit recruit = Recruit.builder()
                    .id((long)index + 1)
                    .team(team)
                    .title("test title " + index)
                    .link("test link " + index)
                    .thumbnailUrl("test image " + index)
                    .status(status)
                    .build();
            RecruitFavorite recruitFavorite = new RecruitFavorite(
                    user.getId(), recruit.getId(), user, recruit, true
            );
            recruitFavoriteList.add(recruitFavorite);
        }
        user.setRecruitFavorites(recruitFavoriteList);
    }

    @Test
    @DisplayName("Test getFavorite")
    public void getFavoriteTest() throws Exception {
        // given
        String jwt = tokenProvider.createAccessToken(user);
        // when
        when(favoriteService.getFavorite(any(PrincipalDetails.class), anyString(), anyInt(), anyInt())).thenReturn(favoritePage);
        // then
        mvc.perform(get("/api/v1/recruit/favorite")
                        .with(SecurityMockMvcRequestPostProcessors.user(user.getName()))
                        .header("Authorization", "Bearer " + jwt)
                        .param("type", "study")
                        .param("page", String.valueOf(1))
                        .param("pagesize", String.valueOf(10)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test deleteAll")
    public void deleteAllTest() throws Exception {
        // given
        String jwt = tokenProvider.createAccessToken(user);
        // when
        // then
        mvc.perform(get("/api/v1/recruit/favorite")
                        .with(SecurityMockMvcRequestPostProcessors.user(user.getName()))
                        .header("Authorization", "Bearer " + jwt)
                        .param("type", "study")
                )
                .andExpect(status().isCreated());
    }
}
