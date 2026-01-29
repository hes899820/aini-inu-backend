package scit.ainiinu.pet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scit.ainiinu.common.response.ApiResponse;
import scit.ainiinu.pet.dto.response.BreedResponse;
import scit.ainiinu.pet.dto.response.PersonalityResponse;
import scit.ainiinu.pet.service.PetService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PetController {

    private final PetService petService;

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
}
