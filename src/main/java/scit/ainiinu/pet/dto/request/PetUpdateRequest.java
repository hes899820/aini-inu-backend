package scit.ainiinu.pet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PetUpdateRequest {

    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 10, message = "이름은 10자를 초과할 수 없습니다.")
    private String name;

    @NotNull(message = "나이는 필수입니다.")
    private Integer age;

    private Boolean isNeutered;
    private String mbti;
    private String photoUrl;

    // 성향과 산책 스타일은 ID/Code 리스트로 받습니다.
    private List<Long> personalityIds;
    private List<String> walkingStyleCodes;
}
