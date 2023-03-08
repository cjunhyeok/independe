package community.independe.domain.post.enums;

public enum RegionPostType {
    FREE("자유"), TALK("잡담"), RESTAURANT("식당"), PLAY("놀거리"), MEET("만남"), MARKET("장터");

    private final String description;

    RegionPostType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
