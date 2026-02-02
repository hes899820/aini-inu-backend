package scit.ainiinu.member.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scit.ainiinu.member.entity.enums.Gender;

import java.util.List;

@Getter
@NoArgsConstructor
public class MemberCreateRequest {

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하여야 합니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "닉네임은 한글, 영문, 숫자만 사용할 수 있습니다.")
    private String nickname;

    private String profileImageUrl;

    @Min(value = 1, message = "나이는 1살 이상이어야 합니다.")
    @Max(value = 100, message = "나이는 100살 이하여야 합니다.")
    private Integer age;

    private Gender gender;

    @Size(max = 4, message = "MBTI는 4자 이내여야 합니다.")
    private String mbti;

    @Size(max = 50, message = "성격 키워드는 50자 이내여야 합니다.")
    private String personality;

    @Size(max = 200, message = "자기소개는 200자 이내여야 합니다.")
    private String selfIntroduction;

    private List<Long> personalityTypeIds;
}
