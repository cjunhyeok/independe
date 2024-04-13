package community.independe.repository.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public abstract class PageRequestCreator {

    public static PageRequest createPageRequestSortCreatedDateDesc(int page, int size) {
        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
    }
}
