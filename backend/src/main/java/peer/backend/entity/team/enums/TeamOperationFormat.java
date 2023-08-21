package peer.backend.entity.team.enums;

public enum TeamOperationFormat {
    ONLINE("온라인"),
    OFFLINE("오프라인"),
    MIX("혼합");

    private String format;

    private TeamOperationFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }
}
