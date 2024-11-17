package leafmap.server.global.common.exception;

import leafmap.server.global.common.ErrorResponseCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorResponseCode errorCode;

    public CustomException(ErrorResponseCode errorCode) {
        this.errorCode = errorCode;
    }

    public static class NotFoundEntityException extends CustomException {
        public NotFoundEntityException(ErrorResponseCode errorCode) {
            super(errorCode);
        }
    }

    public static class NotFoundUserException extends CustomException {
        public NotFoundUserException(ErrorResponseCode errorCode) {
            super(errorCode);
        }
    }

    public static class ForbiddenException extends CustomException {
        public ForbiddenException(ErrorResponseCode errorCode) {
            super(errorCode);
        }
    }



}
