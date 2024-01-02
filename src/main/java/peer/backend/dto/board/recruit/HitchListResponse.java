package peer.backend.dto.board.recruit;


import lombok.*;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.TagListManager;
import peer.backend.entity.tag.Tag;

@Getter
@Builder
@AllArgsConstructor
public class HitchListResponse {
    private String teamName;
    private String title;
    private Long recruitId;
    private Tag tagList;
    private String image;

    public HitchListResponse(Recruit recruit){
        this.teamName = recruit.getTeam().getName();
        this.title = recruit.getTitle();
        this.recruitId = recruit.getId();
        this.tagList = TagListManager.getRecruitTags()
    }
}
