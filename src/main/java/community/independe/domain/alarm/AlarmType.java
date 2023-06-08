package community.independe.domain.alarm;

public enum AlarmType {

    TALK("채팅"), COMMENT("댓글");
    private final String description;
    AlarmType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
