package community.independe.api.dtos.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostsResponseDto {

    List<PostsResponse> postsResponses;
    List<IndependentPostVideoDto> independentPostVideoDtos;
}
