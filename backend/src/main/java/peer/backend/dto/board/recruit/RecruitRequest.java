package peer.backend.dto.board.recruit;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecruitRequest {
    private String type;
    private String sort;
    private Long page;
    private Long pageSize;
    private String keyword;
    private String due;
    private List<String> region;
    private String place;
    private List<String> tag;
}
