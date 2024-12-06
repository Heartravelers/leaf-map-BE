package leafmap.server.global.oauth2.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal(expression = "@authService.getUserInfo()")
//인증된 사용자의 Principal 정보를 참조할 수 있다.
public @interface CurrentUser {

}
