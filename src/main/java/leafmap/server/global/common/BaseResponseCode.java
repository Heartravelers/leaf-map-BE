package leafmap.server.global.common;

import org.springframework.http.HttpStatus;

public interface BaseResponseCode {

    HttpStatus getHttpStatus();

    String getCode();

    String getMessage();
}
