package leafmap.server.global.oauth2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import leafmap.server.domain.user.dto.OAuthSignInResponse;
import leafmap.server.global.common.ApiResponse;
import leafmap.server.global.oauth2.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "OAuth2", description = "소셜 로그인")
public class AuthController {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Operation(summary = "OAuth2 로그인", description = "OAuth2 인증 코드를 사용해 로그인합니다.")
    @GetMapping("/login/{provider}")
    public ResponseEntity<ApiResponse<OAuthSignInResponse>> oauth2Login(

            @Parameter(description = "OAuth2 인증 코드", required = true) @RequestParam String code,
            @Parameter(description = "OAuth2 제공자 (예: google, kakao, naver)", required = true) @PathVariable String provider) {

        ApiResponse<OAuthSignInResponse> response = customOAuth2UserService.processOAuth2Login(code, provider);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Access Token 재발급", description = "Refresh Token을 사용해 새로운 Access Token을 재발급받습니다.")
    @PostMapping("/reissue-token")
    public ResponseEntity<ApiResponse<OAuthSignInResponse>> reissueAccessToken(
            @Parameter(description = "유효한 Refresh Token", required = true) @RequestParam String refreshToken) {

        ApiResponse<OAuthSignInResponse> response = customOAuth2UserService.reissueAccessToken(refreshToken);
        return ResponseEntity.ok(response);
    }
}


