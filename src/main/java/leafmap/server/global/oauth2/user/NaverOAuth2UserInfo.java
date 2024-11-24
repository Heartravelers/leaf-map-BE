package leafmap.server.global.oauth2.user;

import java.util.Map;

public class NaverOAuth2UserInfo implements OAuth2UserInfo{
    private Map<String, Object> attributes;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
    }
    @Override
    public OAuth2Provider getProvider() {return OAuth2Provider.NAVER;}

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}
