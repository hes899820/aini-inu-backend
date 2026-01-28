package scit.ainiinu.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import scit.ainiinu.common.exception.ErrorCode;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private int status;
    private T data;
    private String errorCode;
    private String message;

    // Factory methods
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, 200, data, null, null);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(
                false,
                errorCode.getHttpStatus().value(),
                null,
                errorCode.getCode(),
                errorCode.getMessage()
        );
    }
    
    // For manual error construction (e.g. validation errors)
    public static <T> ApiResponse<T> error(int status, String code, String message, T data) {
        return new ApiResponse<>(false, status, data, code, message);
    }
}
