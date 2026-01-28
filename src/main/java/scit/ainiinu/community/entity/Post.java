package scit.ainiinu.community.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scit.ainiinu.common.entity.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long authorId;  // Member ID 참조 (Member Context)
    
    @Column(nullable = false, length = 2000)
    private String content;
    
    // TODO: 나머지 필드(이미지 URL 등)를 추가해보세요.
    
    // TODO: 비즈니스 로직(수정 등)을 위한 메서드를 추가해보세요. (Setter 사용 금지!)
}
