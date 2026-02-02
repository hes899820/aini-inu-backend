package scit.ainiinu.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scit.ainiinu.member.dto.request.MemberCreateRequest;
import scit.ainiinu.member.dto.response.MemberPersonalityTypeResponse;
import scit.ainiinu.member.dto.response.MemberResponse;
import scit.ainiinu.member.entity.Member;
import scit.ainiinu.member.entity.MemberPersonality;
import scit.ainiinu.member.entity.MemberPersonalityType;
import scit.ainiinu.member.exception.MemberErrorCode;
import scit.ainiinu.member.exception.MemberException;
import scit.ainiinu.member.repository.MemberPersonalityRepository;
import scit.ainiinu.member.repository.MemberPersonalityTypeRepository;
import scit.ainiinu.member.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberPersonalityTypeRepository memberPersonalityTypeRepository;
    private final MemberPersonalityRepository memberPersonalityRepository;

    /**
     * 회원가입 완료 (프로필 생성)
     *
     * @param memberId 인증된 회원 ID
     * @param request  프로필 정보
     * @return 갱신된 회원 정보
     */
    @Transactional
    public MemberResponse createProfile(Long memberId, MemberCreateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 닉네임 중복 검사 (현재 닉네임과 다를 경우에만)
        if (!member.getNickname().equals(request.getNickname()) &&
                memberRepository.existsByNickname(request.getNickname())) {
            throw new MemberException(MemberErrorCode.DUPLICATE_NICKNAME);
        }

        // 회원 정보 업데이트
        member.updateProfile(
                request.getNickname(),
                request.getProfileImageUrl(),
                null, // linkedNickname은 초기 생성 시 null
                request.getAge(),
                request.getGender(),
                request.getMbti(),
                request.getPersonality(),
                request.getSelfIntroduction()
        );

        // 성격 유형 매핑 저장
        List<MemberPersonalityTypeResponse> personalityTypeResponses = updateMemberPersonalities(member, request.getPersonalityTypeIds());

        return MemberResponse.from(member, personalityTypeResponses);
    }

    private List<MemberPersonalityTypeResponse> updateMemberPersonalities(Member member, List<Long> typeIds) {
        // 기존 매핑 삭제 (수정 시에도 활용 가능)
        memberPersonalityRepository.deleteByMember(member);
        
        if (typeIds == null || typeIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<MemberPersonalityType> types = memberPersonalityTypeRepository.findAllById(typeIds);
        

        List<MemberPersonality> newPersonalities = types.stream()
                .map(type -> MemberPersonality.builder()
                        .member(member)
                        .personalityType(type)
                        .build())
                .collect(Collectors.toList());

        memberPersonalityRepository.saveAll(newPersonalities);

        return types.stream()
                .map(MemberPersonalityTypeResponse::from)
                .collect(Collectors.toList());
    }
}
