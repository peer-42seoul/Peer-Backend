package peer.backend.dto.profile.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import peer.backend.dto.profile.SkillDTO;
import peer.backend.entity.tag.Tag;

import java.util.List;

@Getter
@Setter
@Builder
public class PortfolioDTO {
    private Long teamId;
    private List<SkillDTO> tagList;
    private String teamName;
    private String teamLogo;
    private String recruitImage;
    private List<Long> redirectionIds;
    @JsonProperty("isEnd")
    private boolean end;
}
