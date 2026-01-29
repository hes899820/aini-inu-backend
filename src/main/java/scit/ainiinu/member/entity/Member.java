package scit.ainiinu.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scit.ainiinu.common.entity.BaseTimeEntity;
import scit.ainiinu.common.exception.BusinessException;
import scit.ainiinu.member.entity.enums.MemberType;
import scit.ainiinu.member.entity.enums.SocialProvider;
import scit.ainiinu.member.entity.vo.MannerTemperature;
import scit.ainiinu.member.exception.MemberErrorCode;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberType memberType;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_provider")
    private SocialProvider socialProvider;

    @Column(name = "social_id")
    private String socialId;

    @Embedded
    private MannerTemperature mannerTemperature;

    private int mannerScoreSum;
    private int mannerScoreCount;

    @Builder
    public Member(String email, String nickname, String profileImageUrl, MemberType memberType, SocialProvider socialProvider, String socialId) {
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.memberType = memberType;
        this.socialProvider = socialProvider;
        this.socialId = socialId;
        this.mannerTemperature = new MannerTemperature(new java.math.BigDecimal("5.0")); // Default value
        this.mannerScoreSum = 0;
        this.mannerScoreCount = 0;
    }

    public void addMannerScore(int score) {
        if (score < 1 || score > 10) {
            throw new BusinessException(MemberErrorCode.INVALID_MANNER_SCORE);
        }
        this.mannerScoreSum += score;
        this.mannerScoreCount += 1;
        this.mannerTemperature = MannerTemperature.fromAverage(this.mannerScoreSum, this.mannerScoreCount);
    }

    public void updateProfile(String nickname, String profileImageUrl) {
        if (nickname != null && !nickname.isBlank()) {
            this.nickname = nickname;
        }
        if (profileImageUrl != null && !profileImageUrl.isBlank()) {
            this.profileImageUrl = profileImageUrl;
        }
    }
}
