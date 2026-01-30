package scit.ainiinu.community.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import scit.ainiinu.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum CommunityErrorCode implements ErrorCode {
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "CO001", "게시물을 찾을 수 없습니다"),
    NOT_POST_OWNER(HttpStatus.FORBIDDEN, "CO002", "게시물 작성자가 아닙니다"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "CO003", "댓글을 찾을 수 없습니다"),
    NOT_COMMENT_OWNER(HttpStatus.FORBIDDEN, "CO004", "댓글 작성자가 아닙니다"),
    INVALID_CONTENT_LENGTH(HttpStatus.BAD_REQUEST, "CO005", "내용이 너무 깁니다"),
    
    // 기존 유지 (명세 외 추가 제약)
    EXCEEDED_IMAGE_COUNT(HttpStatus.BAD_REQUEST, "CO901", "이미지는 최대 5개까지 업로드 가능합니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
