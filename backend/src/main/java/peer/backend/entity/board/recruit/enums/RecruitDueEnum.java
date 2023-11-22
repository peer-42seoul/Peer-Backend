package peer.backend.entity.board.recruit.enums;

import lombok.Getter;


@Getter
public enum RecruitDueEnum {
    ONE_WEEK(1, "1주일"),
    TWO_WEEKS(2, "2주일"),
    THREE_WEEKS(3, "3주일"),
    FOUR_WEEKS(4, "4주일"),
    ONE_MONTH(5, "1개월"),
    TWO_MONTHS(6, "2개월")
    ;
    // ... 나머지 값들 ...

    private final int value;
    private final String label;

    RecruitDueEnum(int value, String label) {
        this.value = value;
        this.label = label;
    }

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

