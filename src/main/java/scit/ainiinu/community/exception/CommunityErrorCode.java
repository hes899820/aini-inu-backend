package scit.ainiinu.community.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import scit.ainiinu.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum CommunityErrorCode implements ErrorCode {
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "CO001", "게시글을 찾을 수 없습니다"),
    INVALID_CONTENT(HttpStatus.BAD_REQUEST, "CO002", "게시글 내용은 비어있을 수 없으며 2000자를 초과할 수 없습니다"),
    EXCEEDED_IMAGE_COUNT(HttpStatus.BAD_REQUEST, "CO003", "이미지는 최대 5개까지 업로드 가능합니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
