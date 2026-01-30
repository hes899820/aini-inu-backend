package scit.ainiinu.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//게시글작성
@Data
@NoArgsConstructor
public class PostCreateRequest {
    @NotBlank
    @Size(max = 2000)
    private String content;

    @Size(max = 5)
    private List<String> imageUrls;
}
