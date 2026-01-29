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
    @Column(nullable = false, columnDefinition = "TEXT")
    private String imageUrls; // JSON 배열 문자열

    @Column(nullable = false)
    private int likeCount = 0;

    @Column(nullable = false)
    private int commentCount = 0;


    // TODO: 비즈니스 로직(수정 등)을 위한 메서드를 추가해보세요. (Setter 사용 금지!)

    //게시글 수정
    public void update(String content, String imageUrls){
        this.content = content;
        this.imageUrls = imageUrls;
    }
    //좋아요 +1
    public void increaseLike(){
        this.likeCount++;
    }
    //좋아요 취소
    public void decreaseLike(){
        if(this.likeCount>0) this.likeCount--;
    }
    //댓글 추가
    public void increaseComment(){
        this.commentCount++;
    }
    //댓글 삭제
    public void decreaseComment(){
        if(this.commentCount>0) this.commentCount--;
    }

}
