package peer.backend.dto.profile.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.dto.profile.SkillDTO;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.RecruitFavorite;
import peer.backend.entity.tag.RecruitTag;
import peer.backend.entity.tag.Tag;
import peer.backend.entity.team.Team;
import peer.backend.entity.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"favorite"})
public class RecruitFavoriteDto {
    private Long recruit_id;
    private String title;
    private String image;
    private Long userId;
    private String userNickname;
    private String userImage;
    private String status;
    private List<SkillDTO> skillList;
    @JsonProperty("isFavorite")
    private boolean isFavorite;

    public RecruitFavoriteDto(RecruitFavorite favorite){
        Recruit recruit = favorite.getRecruit();
        User writer = recruit.getWriter();
        this.recruit_id = recruit.getId();
        this.title = recruit.getTitle();
        this.image = recruit.getThumbnailUrl();
        this.userId = recruit.getWriterId();
        this.userNickname = (writer == null) ? "탈퇴한 유저" : writer.getNickname();
        this.userImage = (writer == null) ? null : writer.getImageUrl();
        this.status = recruit.getStatus().getStatus();
        this.skillList = recruit.getRecruitTags().stream().map(RecruitTag::getSkillFromTag).collect(Collectors.toList());
        this.isFavorite = true;
    }
}
