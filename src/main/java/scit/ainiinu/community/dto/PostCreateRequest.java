package scit.ainiinu.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
//게시글작성시서버로요청
public class PostCreateRequest {
    @NotBlank
    @Size(max = 2000)
    private String content;

    @NotBlank
    private String imageUrls;
}
