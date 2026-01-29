package scit.ainiinu.pet.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scit.ainiinu.common.entity.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pet extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long memberId; // Member Context 참조 (ID only)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breed_id")
    private Breed breed;
    
    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private scit.ainiinu.pet.entity.enums.PetGender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private scit.ainiinu.pet.entity.enums.PetSize size;

    @Column(length = 4)
    private String mbti;

    @Column(nullable = false)
    private Boolean isNeutered;

    private String photoUrl;

    @Column(nullable = false)
    private Boolean isMain;

    @Column(length = 20)
    private String certificationNumber;
    
    // TODO: Personality와의 다대다 관계는 연결 테이블(PetPersonality)을 통해 구현해야 합니다.
}
