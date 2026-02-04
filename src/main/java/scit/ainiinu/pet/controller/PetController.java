package scit.ainiinu.pet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scit.ainiinu.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import scit.ainiinu.pet.dto.request.PetCreateRequest;
import scit.ainiinu.pet.dto.request.PetUpdateRequest;
import scit.ainiinu.pet.dto.response.PetResponse;
import scit.ainiinu.pet.dto.response.BreedResponse;
import scit.ainiinu.pet.dto.response.PersonalityResponse;
import scit.ainiinu.pet.dto.response.WalkingStyleResponse;
import scit.ainiinu.pet.service.PetService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PetController {

    private final PetService petService;

    /**
     * 반려견 등록
     */
    @PostMapping("/pets")
    public ResponseEntity<ApiResponse<PetResponse>> createPet(
//            @scit.ainiinu.common.security.annotation.CurrentMember Long memberId,
            @Valid @RequestBody PetCreateRequest request
    ) {
        long memberId = 1L; //하드 코딩으로 인증
        PetResponse response = petService.createPet(memberId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 반려견 정보 수정
     */
    @PatchMapping("/pets/{petId}")
    public ResponseEntity<ApiResponse<PetResponse>> updatePet(
            @PathVariable Long petId,
            @Valid @RequestBody PetUpdateRequest request
            // @CurrentMember Long memberId
    ) {
        long memberId = 1L; // 하드 코딩으로 인증
        PetResponse response = petService.updatePet(memberId, petId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 내 반려견 목록 조회
     */
    @GetMapping("/pets")
    public ResponseEntity<ApiResponse<List<PetResponse>>> getMyPets(
//            @scit.ainiinu.common.security.annotation.CurrentMember Long memberId
    ) {
        long memberId = 1L; // 하드 코딩으로 인증
        List<PetResponse> response = petService.getUserPets(memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 견종 목록 조회
     */
    @GetMapping("/breeds")
    public ResponseEntity<ApiResponse<List<BreedResponse>>> getBreeds() {
        List<BreedResponse> response = petService.getAllBreeds();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 성격 카테고리 목록 조회
     */
    @GetMapping("/personalities")
    public ResponseEntity<ApiResponse<List<PersonalityResponse>>> getPersonalities() {
        List<PersonalityResponse> response = petService.getAllPersonalities();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 산책 스타일 목록 조회
     */
    @GetMapping("/walking-styles")
    public ResponseEntity<ApiResponse<List<WalkingStyleResponse>>> getWalkingStyles() {
        List<WalkingStyleResponse> response = petService.getAllWalkingStyles();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
