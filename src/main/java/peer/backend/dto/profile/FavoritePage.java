package peer.backend.dto.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import peer.backend.dto.board.recruit.RecruitListResponse;

import java.util.List;

@Getter
@Setter
@Builder
@JsonIgnoreProperties(value = {"last"})
public class FavoritePage {
    List<RecruitListResponse> postList;
    @JsonProperty("isLast")
    boolean isLast;
}
