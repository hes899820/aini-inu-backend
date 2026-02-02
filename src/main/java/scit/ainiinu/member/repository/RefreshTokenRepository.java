package scit.ainiinu.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scit.ainiinu.member.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void deleteByTokenHash(String tokenHash);
    Optional<RefreshToken> findByMemberId(Long memberId);
}
