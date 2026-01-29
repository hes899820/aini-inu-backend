package scit.ainiinu.member.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import scit.ainiinu.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "ME001", "회원을 찾을 수 없습니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "ME002", "이미 사용 중인 닉네임입니다."),
    INVALID_MANNER_SCORE(HttpStatus.BAD_REQUEST, "ME003", "유효하지 않은 매너 점수입니다."),
    ALREADY_BLOCKED(HttpStatus.CONFLICT, "ME004", "이미 차단한 회원입니다."),
    NOT_BLOCKED(HttpStatus.BAD_REQUEST, "ME005", "차단하지 않은 회원입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
