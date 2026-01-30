package scit.ainiinu.pet.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import scit.ainiinu.pet.dto.response.BreedResponse;
import scit.ainiinu.pet.dto.response.PersonalityResponse;
import scit.ainiinu.pet.entity.enums.PetSize;
import scit.ainiinu.pet.service.PetService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PetController.class)
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PetService petService;

    @Test
    @DisplayName("견종 목록 조회 API 성공 테스트")
    @WithMockUser // 인증된 사용자로 가정 (보안 통과)
    void getBreeds_Success() throws Exception {
        // given: 서비스가 반환할 가짜 데이터 정의
        List<BreedResponse> mockResponse = List.of(
                new BreedResponse(1L, "말티즈", PetSize.SMALL),
                new BreedResponse(2L, "골든 리트리버", PetSize.LARGE)
        );
        given(petService.getAllBreeds()).willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/v1/breeds")
                        .with(csrf())) // CSRF 토큰 처리
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("말티즈"))
                .andExpect(jsonPath("$.data[1].name").value("골든 리트리버"));
    }

    @Test
    @DisplayName("성격 목록 조회 API 성공 테스트")
    @WithMockUser
    void getPersonalities_Success() throws Exception {
        // given
        List<PersonalityResponse> mockResponse = List.of(
                new PersonalityResponse(1L, "소심해요", "SHY"),
                new PersonalityResponse(2L, "활발해요", "ACTIVE")
        );
        given(petService.getAllPersonalities()).willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/v1/personalities")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("소심해요"))
                .andExpect(jsonPath("$.data[0].code").value("SHY"));
    }
}