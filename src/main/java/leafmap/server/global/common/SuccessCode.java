package leafmap.server.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode implements BaseResponseCode {

    // 가장 일반적인 성공 응답
    OK(HttpStatus.OK, "SUCCESS200", "요청이 성공적으로 처리되었습니다."),
    CREATED(HttpStatus.CREATED, "SUCCESS201", "자원이 성공적으로 생성되었습니다."),
    ACCEPTED(HttpStatus.ACCEPTED, "SUCCESS202", "요청이 접수되었습니다.")

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}