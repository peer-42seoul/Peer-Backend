package peer.backend.entity.team.enums;

public enum TeamMemberStatus {
    CONFIRMED("확정"),
    RECRUITING("모집 중");

    private String status;

    private TeamMemberStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
