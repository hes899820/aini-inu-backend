package scit.ainiinu.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import scit.ainiinu.member.entity.Member;
import scit.ainiinu.member.entity.enums.SocialProvider;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findBySocialProviderAndSocialId(SocialProvider socialProvider, String socialId);
    boolean existsByNickname(String nickname);
    Optional<Member> findByEmail(String email);
}
