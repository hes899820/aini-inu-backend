package scit.ainiinu.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import scit.ainiinu.common.security.interceptor.JwtAuthInterceptor;
import scit.ainiinu.common.security.resolver.CurrentMemberArgumentResolver;
import scit.ainiinu.member.dto.request.MemberCreateRequest;
import scit.ainiinu.member.dto.response.MemberResponse;
import scit.ainiinu.member.entity.enums.Gender;
import scit.ainiinu.member.service.MemberService;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private JwtAuthInterceptor jwtAuthInterceptor;

    @MockitoBean
    private CurrentMemberArgumentResolver currentMemberArgumentResolver;

    @BeforeEach
    void setUp() throws Exception {
        given(jwtAuthInterceptor.preHandle(any(), any(), any())).willReturn(true);
        // ArgumentResolver가 memberId 1L을 반환하도록 설정 (실제 동작과 유사하게)
        given(currentMemberArgumentResolver.supportsParameter(any())).willReturn(true);
        given(currentMemberArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("회원 프로필 생성 성공 테스트")
    void createProfile_Success() throws Exception {
        // given
        MemberCreateRequest request = new MemberCreateRequest();
        ReflectionTestUtils.setField(request, "nickname", "건홍이네");
        ReflectionTestUtils.setField(request, "age", 29);
        ReflectionTestUtils.setField(request, "gender", Gender.MALE);
        
        MemberResponse response = MemberResponse.builder()
                .id(1L)
                .nickname("건홍이네")
                .age(29)
                .gender(Gender.MALE)
                .personalityTypes(new ArrayList<>())
                .build();

        given(memberService.createProfile(eq(1L), any(MemberCreateRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/members/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nickname").value("건홍이네"));
    }

    @Test
    @WithMockUser
    @DisplayName("닉네임 유효성 검사 실패 테스트")
    void createProfile_ValidationFail() throws Exception {
        // given
        MemberCreateRequest request = new MemberCreateRequest();
        ReflectionTestUtils.setField(request, "nickname", ""); // 빈 닉네임

        // when & then
        mockMvc.perform(post("/api/v1/members/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
