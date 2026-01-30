package scit.ainiinu.community.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class PostUpdateRequest {

    @Size(max = 2000, message = "게시글 내용은 2000자를 초과할 수 없습니다.")
    private String content;

    private List<String> imageUrls;
}
