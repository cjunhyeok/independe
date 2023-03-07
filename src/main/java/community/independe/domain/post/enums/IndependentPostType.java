package community.independe.domain.post.enums;

public enum IndependentPostType {

    CLEAN("청소"), WASH("빨레"), COOK("요리"), HEALTH("건강"), ETC("기타");

    private final String description;

    IndependentPostType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
