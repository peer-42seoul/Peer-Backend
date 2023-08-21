package peer.backend.entity.team.enums;

public enum TeamType {
    STUDY("study"),
    PROJECT("project");

    private String type;

    private TeamType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
