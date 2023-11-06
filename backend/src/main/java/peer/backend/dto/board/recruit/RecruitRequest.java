package peer.backend.dto.board.recruit;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecruitRequest {

//    /recruitement?type=study&sort=latest&&page=1&pagesize=9&keword="키워드"&due=1&region="김포"
//            &place="online"&status="ongoing"&tag="java,spring,react"
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
