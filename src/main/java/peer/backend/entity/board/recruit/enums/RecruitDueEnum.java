package peer.backend.entity.board.recruit.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum RecruitDueEnum {
    ONE_WEEK(1, "1주일"),
    TWO_WEEKS(2, "2주일"),
    THREE_WEEKS(3, "3주일"),
    FOUR_WEEKS(4, "4주일"),
    ONE_MONTH(5, "1개월"),
    TWO_MONTHS(6, "2개월"),
    THREE_MONTHS(7, "3개월"),
    FOUR_MONTHS(8, "4개월"),
    FIVE_MONTHS(9, "5개월"),
    SIX_MONTHS(10, "6개월"),
    SEVEN_MONTHS(11, "7개월"),
    EIGHT_MONTHS(12, "8개월"),
    NINE_MONTHS(13, "9개월"),
    TEN_MONTHS(14, "10개월"),
    ELEVEN_MONTHS(15, "11개월"),
    TWELVE_MONTHS(16, "12개월"),
    TWELVE_ABOVE(17, "12개월 이상")
    ;

    private final int value;
    private final String label;

    public int getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    // 문자열로부터 enum 값을 찾는 정적 메소드
    public static RecruitDueEnum from(String label) {
        for (RecruitDueEnum d : RecruitDueEnum.values()) {
            if (d.label.equalsIgnoreCase(label)) {
                return d;
            }
        }
        throw new IllegalArgumentException("잘못된 기간입니다.");
    }
}

