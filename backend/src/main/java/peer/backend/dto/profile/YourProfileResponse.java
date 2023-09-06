package peer.backend.dto.profile;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import peer.backend.entity.user.UserLink;

/**
 * 닉네임
 * 프로필 이미지
 * 자기 소개
 * 대표 업적
 * 링크 모듈
 */
@Getter
@Builder
public class YourProfileResponse {
    private Long id;
    private String profileImageUrl;
    private String introduction;
    private String representAchievement;
    private List<UserLink> linkList;
}
