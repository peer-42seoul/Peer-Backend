package peer.backend.dto.board.team;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"published", "public"})
public class ShowcasePageInfoResponse {
    @JsonProperty("isPublished")
    private boolean isPublihsed;
    @JsonProperty("isPublic")
    private boolean isPublic;
    private Long showcaseId;
}
