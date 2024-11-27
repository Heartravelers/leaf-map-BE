package leafmap.server.global.oauth2.service;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import leafmap.server.domain.user.dto.OAuthSignInResponse;
import leafmap.server.domain.user.entity.Role;
import leafmap.server.domain.user.entity.User;
import leafmap.server.domain.user.repository.UserRepository;
import leafmap.server.global.common.ApiResponse;
import leafmap.server.global.jwt.JwtTokenProvider;
import leafmap.server.global.oauth2.exception.OAuth2AuthenticationProcessingException;
import leafmap.server.global.oauth2.user.OAuth2UserInfo;
import leafmap.server.global.oauth2.user.OAuth2UserInfoFactory;
import leafmap.server.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.Objects;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;
    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Value("${spring.security.oauth2.client.provider.google.token-uri}")
    private String googleTokenUri;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUri;

    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String naverTokenUri;

    @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
    private String googleUserInfoUri;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String kakaoUserInfoUri;

    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String naverUserInfoUri;

    @Value("${spring.jwt.access-token.expiration-time}")
    private Long accessTokenExpirationTime;

    @Value("${spring.jwt.refresh-token.expiration-time}")
    private Long refreshTokenExpirationTime;

    @Value("${spring.jwt.redirect}")
    private String redirectUri;


    public ApiResponse<OAuthSignInResponse> processOAuth2Login(String code, String provider) {
        // 1. 소셜 제공자로부터 액세스 토큰 발급
        String accessToken = getAccessTokenResponse(code, provider);
        log.info("accessToken",accessToken);

        // 2. 액세스 토큰으로 사용자 정보 조회
        Map<String, Object> attributes = fetchUserInfo(accessToken, provider);
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, attributes);
        log.info("userInfo",userInfo);

        // 3. 사용자 정보로 DB에 사용자 찾거나 없으면 새로 생성
        User user = findOrCreateUser(userInfo);
        OAuth2UserPrincipal principal = new OAuth2UserPrincipal(user, attributes);

        // 4. 인증 객체 생성 및 SecurityContextHolder에 저장
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 5. JWT 토큰 생성
        String jwtAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        String jwtRefreshToken = jwtTokenProvider.generateRefreshToken();

        // 6. Redis에 리프레시 토큰 저장
        redisUtil.setValues(user.getUsername(), jwtRefreshToken);

        // 7. OAuthSignInResponse 생성 및 반환
        OAuthSignInResponse response = OAuthSignInResponse.from(
                "Bearer",
                jwtAccessToken,
                accessTokenExpirationTime,
                jwtRefreshToken,
                refreshTokenExpirationTime
        );

        return ApiResponse.onSuccess(response);
    }
    public ApiResponse<OAuthSignInResponse> reissueAccessToken(String refreshToken) {
        // 리프레시 토큰의 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        // Redis에서 저장된 리프레시 토큰과 일치하는지 확인
        String username = jwtTokenProvider.getAuthentication(refreshToken).getName();
        String storedRefreshToken = redisUtil.getValues(username);

        if (!refreshToken.equals(storedRefreshToken)) {
            throw new IllegalArgumentException("리프레시 토큰이 일치하지 않습니다.");
        }

        // 새로운 액세스 및 리프레시 토큰 생성
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken();

        // Redis에 새로운 리프레시 토큰 저장
        redisUtil.setValues(username, newRefreshToken);

        // 새로운 토큰 정보를 포함한 응답 객체 생성
        OAuthSignInResponse response = OAuthSignInResponse.from(
                "Bearer",
                newAccessToken,
                accessTokenExpirationTime,
                newRefreshToken,
                refreshTokenExpirationTime
        );

        return ApiResponse.onSuccess(response);
    }



    private String getAccessTokenResponse(String code, String provider) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");


        String tokenUri;
        String clientId;
        String clientSecret;

        switch (provider.toLowerCase()) {
            case "google":
                tokenUri = googleTokenUri;
                clientId = googleClientId;
                clientSecret = googleClientSecret;
                break;
            case "kakao":
                tokenUri = kakaoTokenUri;
                clientId = kakaoClientId;
                clientSecret = kakaoClientSecret;
                break;
            case "naver":
                tokenUri = naverTokenUri;
                clientId = naverClientId;
                clientSecret = naverClientSecret;
                break;
            default:
                throw new IllegalArgumentException("지원하지 않는 provider입니다: " + provider);
        }

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, httpHeaders);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    tokenUri,
                    HttpMethod.POST, // 명시적으로 POST 메서드를 사용
                    request,
                    String.class
            );

            JsonObject jsonObject = JsonParser.parseString(Objects.requireNonNull(response.getBody())).getAsJsonObject();
            if (jsonObject.has("access_token")) {
                return jsonObject.get("access_token").getAsString();
            } else {
                throw new OAuth2AuthenticationProcessingException("Empty access token response from " + provider);
            }
        } catch (Exception e) {
            throw new OAuth2AuthenticationProcessingException("Error fetching access token from " + provider + e.getMessage());
        }
    }

    private Map<String, Object> fetchUserInfo(String accessToken, String provider) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        String userInfoUri;

        switch (provider.toLowerCase()) {
            case "google":
                userInfoUri = googleUserInfoUri;
                break;
            case "kakao":
                userInfoUri = kakaoUserInfoUri;
                break;
            case "naver":
                userInfoUri = naverUserInfoUri;
                break;
            default:
                throw new IllegalArgumentException("지원하지 않는 provider입니다: " + provider);
        }

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, entity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            throw new OAuth2AuthenticationProcessingException("Error fetching user info from " + provider + e.getMessage());
        }
    }

    private User findOrCreateUser(OAuth2UserInfo userInfo) {
        return userRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> userRepository.save(User.builder()
                        .username(userInfo.getName())
                        .email(userInfo.getEmail())
                        .provider(userInfo.getProvider())
                        .providerId(userInfo.getProviderId())
                        .role(Role.ROLE_USER)
                        .build()));
    }
}
