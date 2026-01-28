package scit.ainiinu.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@AllArgsConstructor
public class SliceResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private boolean first;
    private boolean last;
    private boolean hasNext;

    public static <T> SliceResponse<T> of(Slice<T> slice) {
        return new SliceResponse<>(
            slice.getContent(),
            slice.getNumber(),
            slice.getSize(),
            slice.isFirst(),
            slice.isLast(),
            slice.hasNext()
        );
    }
}
