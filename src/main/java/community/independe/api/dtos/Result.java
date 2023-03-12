package community.independe.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {

    private T data;
    private long count;

    public Result(T data) {
        this.data = data;
    }
}
