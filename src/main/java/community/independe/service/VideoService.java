package community.independe.service;

import community.independe.api.dtos.post.IndependentPostVideoDto;
import community.independe.api.dtos.video.FindVideosResponse;
import community.independe.domain.post.enums.IndependentPostType;

import java.util.List;

public interface VideoService {

    List<IndependentPostVideoDto> findAllByIndependentPostType(IndependentPostType independentPostType);
    List<FindVideosResponse> findAll();
}
