package leafmap.server.global.common.exception;

import leafmap.server.global.common.ErrorResponseCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorResponseCode errorCode;

    public CustomException(ErrorResponseCode errorCode) {
        this.errorCode = errorCode;
    }
}
