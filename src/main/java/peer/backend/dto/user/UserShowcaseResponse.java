package peer.backend.dto.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.user.User;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class UserShowcaseResponse {
    private Long id;
    private String image;
    private String nickname;
    private String role;

    public UserShowcaseResponse(TeamUser teamUser){
        User user = teamUser.getUser();
        this.id = user.getId();
        this.image = user.getImageUrl();
        this.nickname = user.getNickname();
        this.role = teamUser.getTeamUserJobs().get(0).getTeamJob().getName();
    }
}
