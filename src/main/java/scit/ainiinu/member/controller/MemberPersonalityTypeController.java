package scit.ainiinu.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scit.ainiinu.common.response.ApiResponse;
import scit.ainiinu.member.dto.response.MemberPersonalityTypeResponse;
import scit.ainiinu.member.service.MemberPersonalityTypeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member-personality-types")
public class MemberPersonalityTypeController {

    private final MemberPersonalityTypeService personalityTypeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberPersonalityTypeResponse>>> getAllPersonalityTypes() {
        List<MemberPersonalityTypeResponse> response = personalityTypeService.getAllPersonalityTypes();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
