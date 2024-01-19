package peer.backend.dto.profile.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.board.recruit.RecruitFavorite;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitFavoriteDto {
    private String type;
    private Long recruitId;
    private Long userId;

    public RecruitFavoriteDto(RecruitFavorite favorite){
        this.type = favorite.getType().getType();
        this.recruitId = favorite.getRecruitId();
        this.userId = favorite.getUserId();
    }
}
