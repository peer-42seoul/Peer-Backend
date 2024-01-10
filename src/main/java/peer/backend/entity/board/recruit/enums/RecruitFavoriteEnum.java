package peer.backend.entity.board.recruit.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecruitFavoriteEnum {
    LIKE("좋아요"),
    DISLIKE("싫어요")
    ;

    private final String type;

    public String getType() {
        return type;
    }

    // 문자열로부터 enum 값을 찾는 정적 메소드
    public static RecruitFavoriteEnum from(String type) {
        for (RecruitFavoriteEnum d : RecruitFavoriteEnum.values()) {
            if (d.type.equalsIgnoreCase(type)) {
                return d;
            }
        }
        throw new IllegalArgumentException("잘못된 타입입니다.");
    }
}
