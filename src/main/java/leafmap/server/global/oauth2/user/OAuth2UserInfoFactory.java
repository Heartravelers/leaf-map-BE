package leafmap.server.global.oauth2.user;

import leafmap.server.global.oauth2.exception.OAuth2AuthenticationProcessingException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        OAuth2Provider provider;
        try {
            provider = OAuth2Provider.valueOf(registrationId.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OAuth2AuthenticationProcessingException("Login with " + registrationId + " is not supported");
        }

        switch (provider) {
            case GOOGLE:
                return new GoogleOAuth2UserInfo(attributes);
            case NAVER:
                return new NaverOAuth2UserInfo(attributes);
            case KAKAO:
                return new KakaoOAuth2UserInfo(attributes);
            default:
                throw new OAuth2AuthenticationProcessingException("Login with " + registrationId + " is not supported");
        }
    }
}
