package scit.ainiinu.pet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import scit.ainiinu.pet.entity.enums.PetGender;
import scit.ainiinu.pet.entity.enums.PetSize;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PetCreateRequest {
    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 10, message = "이름은 10자를 초과할 수 없습니다")
    private String name;

    @NotNull(message = "견종 ID는 필수입니다")
    private Long breedId;

    @NotNull(message = "나이는 필수입니다")
    private Integer age;

    @NotNull(message = "성별은 필수입니다")
    private PetGender gender;

    @NotNull(message = "크기는 필수입니다")
    private PetSize size;

    @Size(max = 4, message = "MBTI는 4자를 초과할 수 없습니다")
    private String mbti;

    @NotNull(message = "중성화 여부는 필수입니다")
    private Boolean isNeutered;

    private String photoUrl;
    
    private Boolean isMain;

    @Size(max = 15, message = "동물등록번호는 15자 이하여야 합니다")
    private String certificationNumber;

    private List<String> walkingStyles; // List of Codes
    private List<Long> personalityIds; // List of IDs
}
