package peer.backend.entity.team.enums;

public enum TeamStatus {
    RECRUITING("모집 중"),
    BEFORE("시작 전"),
    ONGOING("진행 중"),
    COMPLETE("완료");

    private String status;

    private TeamStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
