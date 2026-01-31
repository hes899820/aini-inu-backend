package scit.ainiinu.pet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scit.ainiinu.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import scit.ainiinu.pet.dto.request.PetCreateRequest;
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
    public ResponseEntity<ApiResponse<PetResponse>> createPet(@Valid @RequestBody PetCreateRequest request) {
        // TODO: Security Context에서 현재 로그인한 사용자 ID 가져오기
        Long currentMemberId = 1L; // 임시 하드코딩

        PetResponse response = petService.createPet(currentMemberId, request);
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
