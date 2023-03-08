package community.independe.domain.post.enums;

public enum RegionType {
    ALL("전체"), SEOUL("서울"), ULSAN("울산"), PUSAN("부산"), KYEONGNAM("경남");

    private final String description;

    RegionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
